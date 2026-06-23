package com.schemaforge.export.dto;

import com.schemaforge.export.entity.ExportDialect;
import jakarta.validation.constraints.NotNull;

public record CreateExportRequest(

        @NotNull(message = "Dialect is required")
        ExportDialect dialect
) {
}