package com.schemaforge.dashboard.dto;

import com.schemaforge.schema.entity.NormalizationTarget;
import com.schemaforge.schema.entity.SchemaStatus;

import java.time.Instant;
import java.util.UUID;

public record RecentSchemaResponse(
        UUID id,
        UUID projectId,
        String projectName,
        String systemName,
        String description,
        NormalizationTarget normalizationTarget,
        SchemaStatus status,
        Integer currentVersion,
        int tableCount,
        Instant updatedAt
) {
}