package com.travelMemory.common;

import com.travelMemory.common.exception.BusinessException;
import com.travelMemory.common.exception.ValidationException;
import com.travelMemory.exception.AccessDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for the application.
 * Handles all exceptions thrown by controllers and services,
 * converting them to appropriate HTTP responses with unified error format.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles AccessDeniedException - thrown when user lacks permission to access a resource.
     * Returns 403 Forbidden status.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, ex.getMessage()));
    }

    /**
     * Handles ValidationException - thrown when input validation fails.
     * Returns 400 Bad Request status.
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<?>> handleValidationException(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    /**
     * Handles BusinessException - thrown for business logic violations.
     * Returns appropriate HTTP status based on exception code.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
        logger.warn("Business error (code: {}): {}", ex.getCode(), ex.getMessage());
        int status = ex.getCode() >= 400 && ex.getCode() < 600 ? ex.getCode() : 500;
        return ResponseEntity.status(status)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * Handles ResourceNotFoundException - thrown when a requested resource is not found.
     * Returns 404 Not Found status.
     */
    @ExceptionHandler(com.travelMemory.common.exception.ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(com.travelMemory.common.exception.ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    /**
     * Handles IllegalArgumentException - thrown for invalid arguments.
     * Converts to ValidationException response with 400 Bad Request status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, ex.getMessage()));
    }

    /**
     * Handles MethodArgumentNotValidException - thrown when request body validation fails.
     * Returns 400 Bad Request status with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ApiResponse.FieldError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(ApiResponse.FieldError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .build());
        });
        logger.warn("Validation failed for {} fields", errors.size());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "Validation failed", errors));
    }

    /**
     * Handles all other exceptions - catches any unhandled exceptions.
     * Returns 500 Internal Server Error status.
     * Logs full stack trace for debugging purposes.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Internal server error"));
    }
}
