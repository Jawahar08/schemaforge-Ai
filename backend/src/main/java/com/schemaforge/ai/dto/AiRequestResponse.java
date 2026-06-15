package com.schemaforge.ai.dto;

import com.schemaforge.ai.entity.AiProvider;
import com.schemaforge.ai.entity.AiRequestStatus;
import com.schemaforge.ai.entity.AiRequestType;

import java.time.Instant;
import java.util.UUID;

public record AiRequestResponse(
        UUID id,
        UUID userId,
        UUID schemaId,
        AiRequestType requestType,
        AiProvider provider,
        String model,
        String prompt,
        String response,
        Integer promptTokens,
        Integer completionTokens,
        Integer latencyMs,
        AiRequestStatus status,
        String errorMessage,
        Instant createdAt
) {
}