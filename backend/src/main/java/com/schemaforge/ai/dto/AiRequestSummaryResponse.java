package com.schemaforge.ai.dto;

import com.schemaforge.ai.entity.AiProvider;
import com.schemaforge.ai.entity.AiRequestStatus;
import com.schemaforge.ai.entity.AiRequestType;

import java.time.Instant;
import java.util.UUID;

public record AiRequestSummaryResponse(
        UUID id,
        UUID schemaId,
        AiRequestType requestType,
        AiProvider provider,
        String model,
        Integer promptTokens,
        Integer completionTokens,
        Integer latencyMs,
        AiRequestStatus status,
        Instant createdAt
) {
}