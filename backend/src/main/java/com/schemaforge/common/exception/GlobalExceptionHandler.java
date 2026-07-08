package com.schemaforge.common.exception;

import com.schemaforge.common.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "API exception [{}]: {}",
                ex.getErrorCode(),
                ex.getMessage()
        );

        ErrorResponse body = ErrorResponse.of(
                ex.getMessage(),
                ex.getErrorCode(),
                ex.getStatus().value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(body);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(
            RateLimitExceededException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Rate limit exceeded [{}]: {}",
                ex.getErrorCode(),
                ex.getMessage()
        );

        ErrorResponse body = ErrorResponse.of(
                ex.getMessage(),
                ex.getErrorCode(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        ErrorResponse body = ErrorResponse.ofValidation(
                "Validation failed",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                "Invalid email or password",
                "INVALID_CREDENTIALS",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabled(
            DisabledException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                "Account is not active. Please verify your email.",
                "ACCOUNT_DISABLED",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                "Authentication failed",
                "UNAUTHORIZED",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                "You do not have permission to perform this action",
                "ACCESS_DENIED",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                ex.getMessage(),
                "METHOD_NOT_ALLOWED",
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(
            NoSuchElementException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = ErrorResponse.of(
                "Resource not found",
                "RESOURCE_NOT_FOUND",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error(
                "Unhandled exception on {}: {}",
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        ErrorResponse body = ErrorResponse.of(
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}