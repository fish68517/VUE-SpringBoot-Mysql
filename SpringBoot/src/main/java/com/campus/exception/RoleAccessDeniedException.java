package com.campus.exception;

/**
 * Role Access Denied Exception
 * Thrown when a user attempts to access a resource without required role
 */
public class RoleAccessDeniedException extends RuntimeException {
    
    public RoleAccessDeniedException(String message) {
        super(message);
    }
    
    public RoleAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
