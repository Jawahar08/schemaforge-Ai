package com.schemaforge.export.service.impl;

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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final SchemaRepository schemaRepository;
    private final ExportRepository exportRepository;
    private final ExportStrategyFactory strategyFactory;
    private final ExportMapper exportMapper;

    @Override
    @Transactional
    public ExportResponse createExport(User requestedBy, UUID schemaId, CreateExportRequest request) {
        // 1. Resolve schema — validates existence and ownership in one query
        Schema schema = schemaRepository.findActiveByIdAndOwnerId(schemaId, requestedBy.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        // 2. Select strategy for the requested dialect
        ExportDialectStrategy strategy = strategyFactory.getStrategy(request.dialect());

        // 3. Generate SQL — runs outside any blocking IO; pure in-memory computation
        String sql;
        try {
            sql = strategy.generateSql(schema);
        } catch (Exception ex) {
            log.error("SQL generation failed for schema {} dialect {}: {}",
                    schemaId, request.dialect(), ex.getMessage(), ex);

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
                    "Failed to generate SQL for dialect " + request.dialect() + ": " + ex.getMessage(), ex);
        }

        long sizeBytes = sql.getBytes(StandardCharsets.UTF_8).length;

        // 4. Persist the completed export record.
        // NOTE: exports.file_url is VARCHAR(1024) in the V8 migration.
        // For scripts > 1024 bytes (which is almost always the case), this column
        // needs to be migrated to TEXT in a V13 migration before going to production.
        // The entity maps this as String; PostgreSQL will raise a DataException if
        // the content exceeds 1024 chars. Add V13 migration to resolve this.
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

        log.info("Export created: id={} schema={} dialect={} size={}B",
                saved.getId(), schemaId, request.dialect(), sizeBytes);

        return exportMapper.toResponse(saved);
    }

    @Override
    public ExportResponse getExport(User requestedBy, UUID exportId) {
        Export export = exportRepository.findByIdAndOwnerId(exportId, requestedBy.getId())
                .orElseThrow(() -> new ExportNotFoundException(exportId));

        return exportMapper.toResponse(export);
    }

    @Override
    public String downloadExport(User requestedBy, UUID exportId) {
        Export export = exportRepository.findByIdAndOwnerId(exportId, requestedBy.getId())
                .orElseThrow(() -> new ExportNotFoundException(exportId));

        if (export.getStatus() != ExportStatus.COMPLETED) {
            throw new ExportGenerationException(
                    "Export " + exportId + " is not in COMPLETED status (current: " + export.getStatus() + ")");
        }

        String content = export.getContent();
        if (content == null || content.isBlank()) {
            throw new ExportGenerationException("Export " + exportId + " has no content available");
        }

        return content;
    }
}