package com.schemaforge.export.service.impl;

import com.schemaforge.activity.dto.RecordActivityRequest;
import com.schemaforge.activity.entity.ActivityType;
import com.schemaforge.activity.service.ActivityService;
import com.schemaforge.export.dto.CreateExportRequest;
import com.schemaforge.export.dto.ExportResponse;
import com.schemaforge.export.entity.Export;
import com.schemaforge.export.entity.ExportStatus;
import com.schemaforge.export.entity.ExportType;
import com.schemaforge.export.exception.ExportGenerationException;
import com.schemaforge.export.exception.ExportNotFoundException;
import com.schemaforge.export.mapper.ExportMapper;
import com.schemaforge.export.repository.ExportRepository;
import com.schemaforge.export.service.ExportService;
import com.schemaforge.export.strategy.ExportDialectStrategy;
import com.schemaforge.export.strategy.ExportStrategyFactory;
import com.schemaforge.notification.entity.NotificationType;
import com.schemaforge.notification.service.NotificationService;
import com.schemaforge.schema.entity.Schema;
import com.schemaforge.schema.exception.SchemaNotFoundException;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final SchemaRepository schemaRepository;
    private final ExportRepository exportRepository;
    private final ExportStrategyFactory strategyFactory;
    private final ExportMapper exportMapper;
    private final NotificationService notificationService;
    private final ActivityService activityService;

    @Override
    @Transactional
    public ExportResponse createExport(
            User requestedBy,
            UUID schemaId,
            CreateExportRequest request
    ) {
        // 1. Resolve schema and validate ownership.
        Schema schema = schemaRepository
                .findActiveByIdAndOwnerId(
                        schemaId,
                        requestedBy.getId()
                )
                .orElseThrow(
                        () -> new SchemaNotFoundException(schemaId)
                );

        // 2. Resolve SQL generation strategy.
        ExportDialectStrategy strategy =
                strategyFactory.getStrategy(request.dialect());

        // 3. Generate SQL.
        String sql;

        try {
            sql = strategy.generateSql(schema);
        } catch (Exception ex) {
            log.error(
                    "SQL generation failed for schema {} dialect {}: {}",
                    schemaId,
                    request.dialect(),
                    ex.getMessage(),
                    ex
            );

            Export failedExport = Export.builder()
                    .projectId(schema.getProject().getId())
                    .schema(schema)
                    .requestedBy(requestedBy)
                    .exportType(ExportType.SQL)
                    .dialect(request.dialect())
                    .status(ExportStatus.FAILED)
                    .build();

            exportRepository.save(failedExport);

            throw new ExportGenerationException(
                    "Failed to generate SQL for dialect "
                            + request.dialect()
                            + ": "
                            + ex.getMessage(),
                    ex
            );
        }

        long sizeBytes =
                sql.getBytes(StandardCharsets.UTF_8).length;

        // 4. Persist completed export.
        Export export = Export.builder()
                .projectId(schema.getProject().getId())
                .schema(schema)
                .requestedBy(requestedBy)
                .exportType(ExportType.SQL)
                .dialect(request.dialect())
                .status(ExportStatus.COMPLETED)
                .content(sql)
                .fileSizeBytes(sizeBytes)
                .completedAt(Instant.now())
                .build();

        Export saved = exportRepository.save(export);

        // 5. Record export activity.
        try {
            activityService.recordActivity(
                    RecordActivityRequest.forExport(
                            requestedBy,
                            schema.getProject().getId(),
                            ActivityType.EXPORT_CREATED,
                            saved.getId(),
                            "SQL export created for \""
                                    + schema.getSystemName()
                                    + "\" ("
                                    + request.dialect()
                                    + ")",
                            Map.of(
                                    "dialect",
                                    request.dialect().name(),
                                    "schemaName",
                                    schema.getSystemName(),
                                    "exportId",
                                    saved.getId().toString()
                            )
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to record EXPORT_CREATED activity for export {}: {}",
                    saved.getId(),
                    ex.getMessage()
            );
        }

        log.info(
                "Export created: id={} schema={} dialect={} size={}B",
                saved.getId(),
                schemaId,
                request.dialect(),
                sizeBytes
        );

        // 6. Notify user that export is ready.
        try {
            notificationService.createNotification(
                    requestedBy.getId(),
                    NotificationType.EXPORT_READY,
                    "Export ready",
                    "Your "
                            + request.dialect()
                            + " export for schema \""
                            + schema.getSystemName()
                            + "\" is ready.",
                    Map.of(
                            "exportId",
                            saved.getId().toString(),
                            "schemaId",
                            schemaId.toString(),
                            "dialect",
                            request.dialect().name()
                    )
            );
        } catch (Exception ex) {
            log.warn(
                    "Failed to create EXPORT_READY notification for user {}: {}",
                    requestedBy.getId(),
                    ex.getMessage()
            );
        }

        return exportMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExportResponse getExport(
            User requestedBy,
            UUID exportId
    ) {
        Export export = exportRepository
                .findByIdAndOwnerId(
                        exportId,
                        requestedBy.getId()
                )
                .orElseThrow(
                        () -> new ExportNotFoundException(exportId)
                );

        return exportMapper.toResponse(export);
    }

    @Override
    @Transactional(readOnly = true)
    public String downloadExport(
            User requestedBy,
            UUID exportId
    ) {
        Export export = exportRepository
                .findByIdAndOwnerId(
                        exportId,
                        requestedBy.getId()
                )
                .orElseThrow(
                        () -> new ExportNotFoundException(exportId)
                );

        if (export.getStatus() != ExportStatus.COMPLETED) {
            throw new ExportGenerationException(
                    "Export "
                            + exportId
                            + " is not in COMPLETED status (current: "
                            + export.getStatus()
                            + ")"
            );
        }

        String content = export.getContent();

        if (content == null || content.isBlank()) {
            throw new ExportGenerationException(
                    "Export "
                            + exportId
                            + " has no content available"
            );
        }

        return content;
    }
}