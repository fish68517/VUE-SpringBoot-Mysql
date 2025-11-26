package com.travelMemory.common.exception;

/**
 * Exception thrown when input validation fails.
 * Extends BusinessException with HTTP status code 400 (Bad Request).
 * Used for validation errors in request parameters, request body, or business logic validation.
 */
public class ValidationException extends BusinessException {

    /**
     * Creates a ValidationException with HTTP status code 400.
     *
     * @param message the validation error message
     */
    public ValidationException(String message) {
        super(400, message);
    }

    /**
     * Creates a ValidationException with a cause and HTTP status code 400.
     *
     * @param message the validation error message
     * @param cause   the underlying cause
     */
    public ValidationException(String message, Throwable cause) {
        super(400, message);
        initCause(cause);
    }
}
