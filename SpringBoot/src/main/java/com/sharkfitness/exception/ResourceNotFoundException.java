package com.sharkfitness.exception;

/**
 * Exception thrown when a requested resource is not found
 */
public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(404, resourceType + " with id " + id + " not found");
    }
}
