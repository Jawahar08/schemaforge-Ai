package com.schemaforge.export.dto;

import com.schemaforge.export.entity.ExportDialect;
import com.schemaforge.export.entity.ExportStatus;
import com.schemaforge.export.entity.ExportType;

import java.time.Instant;
import java.util.UUID;

public record ExportResponse(
        UUID exportId,
        UUID schemaId,
        ExportType exportType,
        ExportDialect dialect,
        ExportStatus status,
        Long fileSizeBytes,
        Instant createdAt,
        Instant completedAt
) {
}