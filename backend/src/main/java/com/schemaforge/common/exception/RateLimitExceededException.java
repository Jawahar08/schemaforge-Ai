package com.schemaforge.common.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends ApiException {

    public RateLimitExceededException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED");
    }

    public static RateLimitExceededException forOperation(String operation, int maxRequests, int windowSeconds) {
        return new RateLimitExceededException(
                String.format("Rate limit exceeded for %s: maximum %d requests per %d seconds",
                        operation, maxRequests, windowSeconds)
        );
    }
}