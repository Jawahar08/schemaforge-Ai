package com.schemaforge.ai.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaGenerationServiceImpl implements SchemaGenerationService {

    private static final AiProvider DEFAULT_PROVIDER = AiProvider.CLAUDE;
    private static final NormalizationTarget DEFAULT_NORMALIZATION_TARGET = NormalizationTarget.THREE_NF;

    private final AiProviderClientFactory providerClientFactory;
    private final SchemaService schemaService;
    private final SchemaRepository schemaRepository;
    private final AiRequestRepository aiRequestRepository;

    @Override
    @Transactional
    public SchemaResponse generateSchema(User user, GenerateSchemaRequest request) {
        AiProvider provider = request.provider() != null ? request.provider() : DEFAULT_PROVIDER;
        NormalizationTarget normalizationTarget = request.normalizationTarget() != null
                ? request.normalizationTarget()
                : DEFAULT_NORMALIZATION_TARGET;

        AiProviderClient client = providerClientFactory.getClient(provider);

        AiSchemaGenerationResult result;
        AiRequestStatus status;
        String errorMessage = null;

        try {
            result = client.generateSchema(request.description(), normalizationTarget.getValue());
            status = AiRequestStatus.SUCCESS;
        } catch (Exception ex) {
            log.error("Schema generation failed for provider {}: {}", provider, ex.getMessage(), ex);
            recordFailedRequest(user, provider, request, ex.getMessage());
            throw ex;
        }

        CreateSchemaRequest createSchemaRequest = new CreateSchemaRequest(
                result.systemName() != null ? result.systemName() : "Generated Schema",
                result.description(),
                normalizationTarget,
                result.tables() != null ? result.tables() : Collections.emptyList(),
                result.relationships() != null ? result.relationships() : Collections.emptyList(),
                result.normalizationNotes() != null ? result.normalizationNotes() : Collections.emptyList(),
                result.analysisItems() != null ? result.analysisItems() : Collections.emptyList()
        );

        SchemaResponse schemaResponse = schemaService.createSchema(user, request.projectId(), createSchemaRequest);

        Schema savedSchema = schemaRepository.findActiveById(schemaResponse.id())
                .orElseThrow(() -> new SchemaNotFoundException(schemaResponse.id()));

        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .schema(savedSchema)
                .requestType(AiRequestType.SCHEMA_GENERATION)
                .provider(provider)
                .model(result.modelUsed())
                .prompt(buildPromptSummary(request))
                .response(result.rawResponse())
                .promptTokens(result.promptTokens())
                .completionTokens(result.completionTokens())
                .latencyMs((int) result.latencyMs())
                .status(status)
                .errorMessage(errorMessage)
                .build();

        aiRequestRepository.save(aiRequest);

        log.info("Schema generated via {} for project {}: schema {}", provider, request.projectId(), savedSchema.getId());

        return schemaResponse;
    }

    private void recordFailedRequest(User user, AiProvider provider, GenerateSchemaRequest request, String errorMessage) {
        AiRequest aiRequest = AiRequest.builder()
                .user(user)
                .schema(null)
                .requestType(AiRequestType.SCHEMA_GENERATION)
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
    }

    private String buildPromptSummary(GenerateSchemaRequest request) {
        return "Schema generation requested for project " + request.projectId() + ": " + request.description();
    }
}