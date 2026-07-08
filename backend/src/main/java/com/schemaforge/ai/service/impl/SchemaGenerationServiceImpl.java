package com.schemaforge.ai.service.impl;

import com.schemaforge.activity.dto.RecordActivityRequest;
import com.schemaforge.activity.entity.ActivityType;
import com.schemaforge.activity.service.ActivityService;
import com.schemaforge.ai.client.AiProviderClient;
import com.schemaforge.ai.client.AiProviderClientFactory;
import com.schemaforge.ai.client.AiSchemaGenerationResult;
import com.schemaforge.ai.dto.GenerateSchemaRequest;
import com.schemaforge.ai.entity.AiProvider;
import com.schemaforge.ai.entity.AiRequest;
import com.schemaforge.ai.entity.AiRequestStatus;
import com.schemaforge.ai.entity.AiRequestType;
import com.schemaforge.ai.repository.AiRequestRepository;
import com.schemaforge.ai.service.SchemaGenerationService;
import com.schemaforge.notification.entity.NotificationType;
import com.schemaforge.notification.service.NotificationService;
import com.schemaforge.schema.dto.CreateSchemaRequest;
import com.schemaforge.schema.dto.SchemaResponse;
import com.schemaforge.schema.entity.NormalizationTarget;
import com.schemaforge.schema.entity.Schema;
import com.schemaforge.schema.exception.SchemaNotFoundException;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.schema.service.SchemaService;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaGenerationServiceImpl implements SchemaGenerationService {

    private static final AiProvider DEFAULT_PROVIDER = AiProvider.CLAUDE;

    private static final NormalizationTarget DEFAULT_NORMALIZATION_TARGET =
            NormalizationTarget.THREE_NF;

    private final AiProviderClientFactory providerClientFactory;
    private final SchemaService schemaService;
    private final SchemaRepository schemaRepository;
    private final AiRequestRepository aiRequestRepository;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    /*
     * Self-injection via @Lazy ensures calls to transactional internal methods
     * pass through the Spring proxy.
     */
    @Autowired
    @Lazy
    private SchemaGenerationServiceImpl self;

    @Override
    public SchemaResponse generateSchema(
            User user,
            GenerateSchemaRequest request
    ) {
        AiProvider provider =
                request.provider() != null
                        ? request.provider()
                        : DEFAULT_PROVIDER;

        NormalizationTarget normalizationTarget =
                request.normalizationTarget() != null
                        ? request.normalizationTarget()
                        : DEFAULT_NORMALIZATION_TARGET;

        AiProviderClient client =
                providerClientFactory.getClient(provider);

        AiSchemaGenerationResult result;

        try {
            /*
             * External AI call intentionally runs outside a database
             * transaction.
             */
            result = client.generateSchema(
                    request.description(),
                    normalizationTarget.getValue()
            );
        } catch (Exception ex) {
            log.error(
                    "Schema generation failed for provider {}: {}",
                    provider,
                    ex.getMessage(),
                    ex
            );

            self.recordFailedRequestInNewTransaction(
                    user,
                    provider,
                    request,
                    ex.getMessage()
            );

            throw ex;
        }

        return self.persistGenerationResult(
                user,
                request,
                provider,
                normalizationTarget,
                result
        );
    }

    @Transactional
    protected SchemaResponse persistGenerationResult(
            User user,
            GenerateSchemaRequest request,
            AiProvider provider,
            NormalizationTarget normalizationTarget,
            AiSchemaGenerationResult result
    ) {
        CreateSchemaRequest createSchemaRequest =
                new CreateSchemaRequest(
                        result.systemName() != null
                                ? result.systemName()
                                : "Generated Schema",
                        result.description(),
                        normalizationTarget,
                        result.tables() != null
                                ? result.tables()
                                : Collections.emptyList(),
                        result.relationships() != null
                                ? result.relationships()
                                : Collections.emptyList(),
                        result.normalizationNotes() != null
                                ? result.normalizationNotes()
                                : Collections.emptyList(),
                        result.analysisItems() != null
                                ? result.analysisItems()
                                : Collections.emptyList()
                );

        SchemaResponse schemaResponse =
                schemaService.createSchema(
                        user,
                        request.projectId(),
                        createSchemaRequest
                );

        Schema savedSchema =
                schemaRepository
                        .findActiveById(schemaResponse.id())
                        .orElseThrow(
                                () -> new SchemaNotFoundException(
                                        schemaResponse.id()
                                )
                        );

        AiRequest aiRequest =
                AiRequest.builder()
                        .user(user)
                        .schema(savedSchema)
                        .requestType(
                                AiRequestType.SCHEMA_GENERATION
                        )
                        .provider(provider)
                        .model(result.modelUsed())
                        .prompt(buildPromptSummary(request))
                        .response(result.rawResponse())
                        .promptTokens(result.promptTokens())
                        .completionTokens(result.completionTokens())
                        .latencyMs((int) result.latencyMs())
                        .status(AiRequestStatus.SUCCESS)
                        .build();

        aiRequestRepository.save(aiRequest);

        /*
         * Record AI generation activity after the successful AI request
         * audit row has been persisted.
         */
        try {
            activityService.recordActivity(
                    RecordActivityRequest.forSchema(
                            user,
                            request.projectId(),
                            schemaResponse.id(),
                            ActivityType.SCHEMA_GENERATED,
                            schemaResponse.id(),
                            "Schema \""
                                    + (
                                            result.systemName() != null
                                                    ? result.systemName()
                                                    : savedSchema.getSystemName()
                                    )
                                    + "\" generated via AI",
                            Map.of(
                                    "schemaName",
                                    result.systemName() != null
                                            ? result.systemName()
                                            : savedSchema.getSystemName(),
                                    "provider",
                                    provider.name(),
                                    "tableCount",
                                    result.tables() != null
                                            ? result.tables().size()
                                            : 0
                            )
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to record SCHEMA_GENERATED activity for schema {}: {}",
                    schemaResponse.id(),
                    ex.getMessage()
            );
        }

        log.info(
                "Schema generated via {} for project {}: schema id={}",
                provider,
                request.projectId(),
                savedSchema.getId()
        );

        /*
         * Notify user of successful schema generation.
         */
        try {
            notificationService.createNotification(
                    user.getId(),
                    NotificationType.SCHEMA_GENERATED,
                    "Schema generated successfully",
                    "Your AI schema \""
                            + savedSchema.getSystemName()
                            + "\" was generated successfully via "
                            + provider,
                    Map.of(
                            "schemaId",
                            savedSchema.getId().toString(),
                            "projectId",
                            request.projectId().toString(),
                            "provider",
                            provider.name()
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to create SCHEMA_GENERATED notification for user {}: {}",
                    user.getId(),
                    ex.getMessage()
            );
        }

        return schemaResponse;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void recordFailedRequestInNewTransaction(
            User user,
            AiProvider provider,
            GenerateSchemaRequest request,
            String errorMessage
    ) {
        AiRequest aiRequest =
                AiRequest.builder()
                        .user(user)
                        .schema(null)
                        .requestType(
                                AiRequestType.SCHEMA_GENERATION
                        )
                        .provider(provider)
                        .model("unknown")
                        .prompt(buildPromptSummary(request))
                        .response(null)
                        .promptTokens(0)
                        .completionTokens(0)
                        .latencyMs(0)
                        .status(AiRequestStatus.FAILED)
                        .errorMessage(errorMessage)
                        .build();

        aiRequestRepository.save(aiRequest);

        log.warn(
                "Recorded failed AI request audit for user={} provider={}",
                user.getId(),
                provider
        );

        /*
         * Notify user that schema generation failed.
         */
        try {
            notificationService.createNotification(
                    user.getId(),
                    NotificationType.SCHEMA_GENERATED,
                    "Schema generation failed",
                    "Schema generation via "
                            + provider
                            + " failed: "
                            + errorMessage,
                    Map.of(
                            "projectId",
                            request.projectId().toString(),
                            "provider",
                            provider.name(),
                            "error",
                            errorMessage != null
                                    ? errorMessage
                                    : "unknown"
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to create SCHEMA_GENERATED failure notification for user {}: {}",
                    user.getId(),
                    ex.getMessage()
            );
        }
    }

    private String buildPromptSummary(
            GenerateSchemaRequest request
    ) {
        return "Schema generation for project="
                + request.projectId()
                + ": "
                + request.description();
    }
}