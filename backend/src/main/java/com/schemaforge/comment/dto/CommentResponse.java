package com.schemaforge.comment.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID projectId,
        UUID schemaId,
        UUID userId,
        UUID parentCommentId,
        String content,
        String entityReference,
        boolean resolved,
        boolean edited,
        Instant editedAt,
        Instant createdAt,
        Instant updatedAt
) {}
