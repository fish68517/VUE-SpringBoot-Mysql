package com.travelMemory.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Extends BusinessException with HTTP status code 404 (Not Found).
 * Used when trying to access, update, or delete a resource that doesn't exist.
 */
public class ResourceNotFoundException extends BusinessException {

    /**
     * Creates a ResourceNotFoundException with HTTP status code 404.
     *
     * @param message the error message describing which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    /**
     * Creates a ResourceNotFoundException with a resource type and ID.
     *
     * @param resourceType the type of resource (e.g., "User", "TravelRecord")
     * @param resourceId   the ID of the resource that was not found
     */
    public ResourceNotFoundException(String resourceType, Long resourceId) {
        super(404, resourceType + " with id " + resourceId + " not found");
    }

    /**
     * Creates a ResourceNotFoundException with a cause and HTTP status code 404.
     *
     * @param message the error message
     * @param cause   the underlying cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(404, message);
        initCause(cause);
    }
}
