package com.schemaforge.export.controller;

import com.schemaforge.common.dto.ApiResponse;
import com.schemaforge.export.dto.CreateExportRequest;
import com.schemaforge.export.dto.ExportResponse;
import com.schemaforge.export.service.ExportService;
import com.schemaforge.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Exports", description = "Endpoints for generating and downloading SQL export scripts")
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/api/schemas/{schemaId}/exports")
    @Operation(
            summary = "Create SQL export",
            description = "Generates a SQL DDL script for the specified schema and dialect. "
                    + "The user must own the schema. Returns export metadata including exportId."
    )
    public ResponseEntity<ApiResponse<ExportResponse>> createExport(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID schemaId,
            @Valid @RequestBody CreateExportRequest request
    ) {
        ExportResponse response = exportService.createExport(currentUser, schemaId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("SQL export generated successfully", response));
    }

    @GetMapping("/api/exports/{exportId}")
    @Operation(
            summary = "Get export metadata",
            description = "Returns metadata for an export (dialect, status, size, timestamps). "
                    + "Does not include the SQL content — use the /download endpoint for that."
    )
    public ResponseEntity<ApiResponse<ExportResponse>> getExport(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID exportId
    ) {
        ExportResponse response = exportService.getExport(currentUser, exportId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/api/exports/{exportId}/download")
    @Operation(
            summary = "Download SQL script",
            description = "Returns the full generated SQL script as a plain-text file download. "
                    + "Content-Disposition header is set to trigger browser download."
    )
    public ResponseEntity<String> downloadExport(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID exportId
    ) {
        String sql = exportService.downloadExport(currentUser, exportId);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"schema-export-" + exportId + ".sql\"")
                .body(sql);
    }
}