package com.schemaforge.activity.dto;

import com.schemaforge.activity.entity.ActivityEntityType;
import com.schemaforge.activity.entity.ActivityType;

import java.time.Instant;
import java.util.UUID;

public record ActivitySummaryResponse(
        UUID id,
        UUID actorUserId,
        String actorName,
        ActivityType activityType,
        ActivityEntityType entityType,
        UUID entityId,
        String title,
        Instant createdAt
) {
}