package com.schemaforge.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotBlank(message = "Comment content is required")
        @Size(max = 5000, message = "Comment content must not exceed 5000 characters")
        String content
) {}
