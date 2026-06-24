package com.schemaforge.notification.dto;

import com.schemaforge.notification.entity.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationSummaryResponse(
        UUID id,
        NotificationType type,
        String title,
        boolean read,
        Instant createdAt
) {}
