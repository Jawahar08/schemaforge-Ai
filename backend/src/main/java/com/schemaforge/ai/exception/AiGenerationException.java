package com.schemaforge.ai.exception;

import com.schemaforge.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class AiGenerationException extends ApiException {

    public AiGenerationException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "AI_GENERATION_FAILED");
    }

    public AiGenerationException(String message, Throwable cause) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, "AI_GENERATION_FAILED");
        initCause(cause);
    }
}