package com.schemaforge.export.exception;

import com.schemaforge.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ExportGenerationException extends ApiException {

    public ExportGenerationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "EXPORT_GENERATION_FAILED");
    }

    public ExportGenerationException(String message, Throwable cause) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "EXPORT_GENERATION_FAILED");
        initCause(cause);
    }
}