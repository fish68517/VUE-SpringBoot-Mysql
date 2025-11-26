package com.travelMemory.utils;

import com.travelMemory.security.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Utility class for permission checking operations
 * Provides helper methods for common permission verification scenarios
 */
@Component
public class PermissionUtils {

    @Autowired
    private PermissionService permissionService;

    /**
     * Check if the current user is the owner of a travel record
     * @param recordOwnerId the ID of the record owner
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isTravelRecordOwner(Long recordOwnerId) {
        return permissionService.isResourceOwner(recordOwnerId);
    }

    /**
     * Check if the current user can view a travel record
     * @param recordOwnerId the ID of the record owner
     * @param isPublic whether the record is public
     * @return true if the current user can view the record, false otherwise
     */
    public boolean canViewTravelRecord(Long recordOwnerId, boolean isPublic) {
        return permissionService.hasAccessToResource(recordOwnerId, isPublic);
    }

    /**
     * Check if the current user can edit a travel record
     * @param recordOwnerId the ID of the record owner
     * @return true if the current user can edit the record, false otherwise
     */
    public boolean canEditTravelRecord(Long recordOwnerId) {
        return permissionService.isResourceOwner(recordOwnerId);
    }

    /**
     * Check if the current user can delete a travel record
     * @param recordOwnerId the ID of the record owner
     * @return true if the current user can delete the record, false otherwise
     */
    public boolean canDeleteTravelRecord(Long recordOwnerId) {
        return permissionService.isResourceOwner(recordOwnerId);
    }

    /**
     * Check if the current user is the owner of a travel plan
     * @param planOwnerId the ID of the plan owner
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isTravelPlanOwner(Long planOwnerId) {
        return permissionService.isResourceOwner(planOwnerId);
    }

    /**
     * Check if the current user can edit a travel plan
     * @param planOwnerId the ID of the plan owner
     * @return true if the current user can edit the plan, false otherwise
     */
    public boolean canEditTravelPlan(Long planOwnerId) {
        return permissionService.isResourceOwner(planOwnerId);
    }

    /**
     * Check if the current user can delete a travel plan
     * @param planOwnerId the ID of the plan owner
     * @return true if the current user can delete the plan, false otherwise
     */
    public boolean canDeleteTravelPlan(Long planOwnerId) {
        return permissionService.isResourceOwner(planOwnerId);
    }

    /**
     * Check if the current user can delete a comment
     * @param commentAuthorId the ID of the comment author
     * @return true if the current user can delete the comment, false otherwise
     */
    public boolean canDeleteComment(Long commentAuthorId) {
        return permissionService.isResourceOwner(commentAuthorId);
    }

    /**
     * Verify that the current user is the owner of a resource
     * @param resourceOwnerId the ID of the resource owner
     * @throws SecurityException if the current user is not the owner
     */
    public void verifyOwnership(Long resourceOwnerId) {
        permissionService.verifyResourceOwner(resourceOwnerId);
    }

    /**
     * Verify that the current user has access to a resource
     * @param resourceOwnerId the ID of the resource owner
     * @param isPublic whether the resource is public
     * @throws SecurityException if the current user does not have access
     */
    public void verifyAccess(Long resourceOwnerId, boolean isPublic) {
        permissionService.verifyResourceAccess(resourceOwnerId, isPublic);
    }

    /**
     * Get the current authenticated user ID
     * @return the current user ID, or null if not authenticated
     */
    public Long getCurrentUserId() {
        return permissionService.getCurrentUserId();
    }
}
