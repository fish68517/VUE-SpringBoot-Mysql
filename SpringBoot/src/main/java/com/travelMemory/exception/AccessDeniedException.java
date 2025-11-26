package com.travelMemory.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission to access.
 * This exception results in HTTP 403 Forbidden response.
 * Used for permission checks and authorization failures.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Creates an AccessDeniedException with an error message.
     *
     * @param message the error message describing why access was denied
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Creates an AccessDeniedException with an error message and underlying cause.
     *
     * @param message the error message describing why access was denied
     * @param cause   the underlying cause
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
