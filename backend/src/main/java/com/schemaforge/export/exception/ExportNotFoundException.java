package com.schemaforge.export.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class ExportNotFoundException extends ResourceNotFoundException {

    public ExportNotFoundException(UUID id) {
        super("Export not found with id: " + id);
    }
}