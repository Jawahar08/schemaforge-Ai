package com.schemaforge.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentSummaryResponse(
        UUID id,
        UUID schemaId,
        UUID userId,
        String content,
        boolean edited,
        Instant createdAt
) {}
