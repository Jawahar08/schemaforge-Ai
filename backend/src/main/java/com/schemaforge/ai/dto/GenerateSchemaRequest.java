package com.schemaforge.ai.dto;

import com.schemaforge.ai.entity.AiProvider;
import com.schemaforge.schema.entity.NormalizationTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record GenerateSchemaRequest(

        @NotNull(message = "Project id is required")
        UUID projectId,

        @NotBlank(message = "System description is required")
        @Size(min = 10, max = 4000, message = "Description must be between 10 and 4000 characters")
        String description,

        NormalizationTarget normalizationTarget,

        AiProvider provider
) {
}