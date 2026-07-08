package com.schemaforge.activity.dto;

import com.schemaforge.activity.entity.ActivityEntityType;
import com.schemaforge.activity.entity.ActivityType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ActivityResponse(
        UUID id,
        UUID actorUserId,
        String actorName,
        UUID projectId,
        UUID teamId,
        UUID schemaId,
        ActivityType activityType,
        ActivityEntityType entityType,
        UUID entityId,
        String title,
        String description,
        Map<String, Object> metadata,
        Instant createdAt
) {
}