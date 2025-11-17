package com.sharkfitness.exception;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(400, message);
    }
}
