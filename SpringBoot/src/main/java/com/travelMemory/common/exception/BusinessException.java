package com.travelMemory.common.exception;

/**
 * Exception thrown for business logic violations.
 * This is the base exception for all business-related errors.
 * Can be extended for more specific business exceptions.
 */
public class BusinessException extends RuntimeException {

    private int code;

    /**
     * Creates a BusinessException with default HTTP status code 400 (Bad Request).
     *
     * @param message the error message
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * Creates a BusinessException with a specific HTTP status code.
     *
     * @param code    the HTTP status code
     * @param message the error message
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Creates a BusinessException with a cause and default HTTP status code 400.
     *
     * @param message the error message
     * @param cause   the underlying cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    /**
     * Gets the HTTP status code associated with this exception.
     *
     * @return the HTTP status code
     */
    public int getCode() {
        return code;
    }
}
