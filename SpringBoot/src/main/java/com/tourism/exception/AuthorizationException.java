package com.tourism.exception;

/**
 * 权限异常
 */
public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}
