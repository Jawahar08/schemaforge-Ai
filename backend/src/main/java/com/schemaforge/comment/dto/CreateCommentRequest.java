package com.schemaforge.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateCommentRequest(
        @NotBlank(message = "Comment content is required")
        @Size(max = 5000, message = "Comment content must not exceed 5000 characters")
        String content,

        UUID parentCommentId,

        @Size(max = 255, message = "Entity reference must not exceed 255 characters")
        String entityReference
) {}
