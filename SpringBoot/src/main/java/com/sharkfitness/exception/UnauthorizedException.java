package com.sharkfitness.exception;

/**
 * Exception thrown when user is not authenticated or token is invalid
 */
public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException() {
        super(401, "Unauthorized access");
    }
}
