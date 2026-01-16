package com.postgraduate.util;

import com.postgraduate.entity.User;
import com.postgraduate.exception.AuthorizationException;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for authorization checks.
 * Provides methods for verifying user permissions and roles.
 */
@Slf4j
@Component
public class AuthorizationUtil {

    private final UserRepository userRepository;

    public AuthorizationUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the current authenticated user.
     *
     * @return the authenticated User entity
     * @throws ResourceNotFoundException if user is not found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Check if the current user is an admin.
     *
     * @return true if the current user has ADMIN role, false otherwise
     */
    public boolean isAdmin() {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == User.UserRole.ADMIN;
    }

    /**
     * Check if the current user is a regular user.
     *
     * @return true if the current user has USER role, false otherwise
     */
    public boolean isUser() {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == User.UserRole.USER;
    }

    /**
     * Verify that the current user is an admin.
     * Throws AuthorizationException if the user is not an admin.
     *
     * @throws AuthorizationException if the current user is not an admin
     */
    public void requireAdmin() {
        if (!isAdmin()) {
            log.warn("User {} attempted to access admin-only resource", getCurrentUser().getId());
            throw new AuthorizationException("Admin role required for this operation");
        }
    }

    /**
     * Verify that the current user owns the specified resource.
     * Throws AuthorizationException if the user does not own the resource.
     *
     * @param resourceOwnerId the ID of the user who owns the resource
     * @throws AuthorizationException if the current user does not own the resource
     */
    public void requireResourceOwnership(Long resourceOwnerId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(resourceOwnerId) && !isAdmin()) {
            log.warn("User {} attempted to access resource owned by user {}", currentUser.getId(), resourceOwnerId);
            throw new AuthorizationException("You are not authorized to access this resource");
        }
    }

    /**
     * Check if the current user owns the specified resource or is an admin.
     *
     * @param resourceOwnerId the ID of the user who owns the resource
     * @return true if the current user owns the resource or is an admin, false otherwise
     */
    public boolean canAccessResource(Long resourceOwnerId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(resourceOwnerId) || isAdmin();
    }
}
