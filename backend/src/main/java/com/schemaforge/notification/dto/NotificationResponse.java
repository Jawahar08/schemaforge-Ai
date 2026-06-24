package com.schemaforge.notification.dto;

import com.schemaforge.notification.entity.NotificationType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        NotificationType type,
        String title,
        String message,
        Map<String, String> metadata,
        boolean read,
        Instant createdAt
) {}
