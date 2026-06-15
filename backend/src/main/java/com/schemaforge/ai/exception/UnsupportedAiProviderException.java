package com.schemaforge.ai.exception;

import com.schemaforge.common.exception.BadRequestException;

public class UnsupportedAiProviderException extends BadRequestException {

    public UnsupportedAiProviderException(String provider) {
        super("Unsupported AI provider: " + provider);
    }
}