package com.schemaforge.export.service;

import com.schemaforge.export.dto.CreateExportRequest;
import com.schemaforge.export.dto.ExportResponse;
import com.schemaforge.user.entity.User;

import java.util.UUID;

public interface ExportService {

    /**
     * Generates SQL for the schema identified by schemaId, stores the result,
     * and returns export metadata. User must own the schema.
     */
    ExportResponse createExport(User requestedBy, UUID schemaId, CreateExportRequest request);

    /**
     * Returns export metadata (no SQL content) for the given export id.
     * User must own the underlying schema.
     */
    ExportResponse getExport(User requestedBy, UUID exportId);

    /**
     * Returns the raw generated SQL script for download.
     * User must own the underlying schema.
     */
    String downloadExport(User requestedBy, UUID exportId);
}