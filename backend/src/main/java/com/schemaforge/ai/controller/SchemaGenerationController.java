package com.schemaforge.ai.controller;

import com.schemaforge.ai.dto.GenerateSchemaRequest;
import com.schemaforge.ai.service.SchemaGenerationService;
import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.schema.dto.SchemaResponse;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schemas")
@RequiredArgsConstructor
@Tag(name = "AI Schema Generation", description = "Endpoints for generating database schemas from natural language using AI")
public class SchemaGenerationController {

    private final SchemaGenerationService schemaGenerationService;

    @PostMapping("/generate")
    @Operation(
            summary = "Generate a schema from a natural language description",
            description = "Sends the description to an AI provider, generates tables, relationships, normalization notes, "
                    + "and analysis items, saves the schema as version 1, and records an AI request audit entry"
    )
    public ResponseEntity<ApiResponse<SchemaResponse>> generateSchema(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody GenerateSchemaRequest request
    ) {
        SchemaResponse response = schemaGenerationService.generateSchema(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Schema generated successfully", response));
    }
}