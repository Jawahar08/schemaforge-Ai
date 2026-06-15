package com.schemaforge.ai.exception;

import com.schemaforge.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class AiRequestNotFoundException extends ResourceNotFoundException {

    public AiRequestNotFoundException(UUID id) {
        super("AI request not found with id: " + id);
    }
}