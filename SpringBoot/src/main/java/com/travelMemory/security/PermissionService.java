package com.travelMemory.security;

import com.travelMemory.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for checking user permissions and access control
 */
@Service
public class PermissionService {

    /**
     * Get the current authenticated user ID from the security context
     * @return the current user ID, or null if not authenticated
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Check if the current user is the owner of a resource
     * @param resourceOwnerId the ID of the resource owner
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isResourceOwner(Long resourceOwnerId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(resourceOwnerId);
    }

    /**
     * Check if the current user has access to a resource
     * This method can be extended to support different access levels (owner, shared, public)
     * @param resourceOwnerId the ID of the resource owner
     * @param isPublic whether the resource is public
     * @return true if the current user has access, false otherwise
     */
    public boolean hasAccessToResource(Long resourceOwnerId, boolean isPublic) {
        // Owner always has access
        if (isResourceOwner(resourceOwnerId)) {
            return true;
        }
        // Non-owners can only access public resources
        return isPublic;
    }

    /**
     * Verify that the current user is the owner of a resource
     * @param resourceOwnerId the ID of the resource owner
     * @throws AccessDeniedException if the current user is not the owner
     */
    public void verifyResourceOwner(Long resourceOwnerId) {
        if (!isResourceOwner(resourceOwnerId)) {
            throw new AccessDeniedException("Access denied: You do not have permission to access this resource");
        }
    }

    /**
     * Verify that the current user has access to a resource
     * @param resourceOwnerId the ID of the resource owner
     * @param isPublic whether the resource is public
     * @throws AccessDeniedException if the current user does not have access
     */
    public void verifyResourceAccess(Long resourceOwnerId, boolean isPublic) {
        if (!hasAccessToResource(resourceOwnerId, isPublic)) {
            throw new AccessDeniedException("Access denied: You do not have permission to access this resource");
        }
    }
}
