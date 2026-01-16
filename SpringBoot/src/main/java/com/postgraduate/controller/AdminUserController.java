package com.postgraduate.controller;

import com.postgraduate.dto.AdminUserDTO;
import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.PasswordResetResponse;
import com.postgraduate.dto.StatusUpdateRequest;
import com.postgraduate.service.AdminUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for admin user management endpoints.
 * Handles user listing, filtering, status updates, and password resets.
 * All endpoints require ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    /**
     * Get all users with optional filtering by status and search keyword.
     * Supports pagination with default page size of 20.
     * Status values: ENABLED, DISABLED
     *
     * @param keyword optional search keyword to filter by username
     * @param status optional user status to filter by (ENABLED/DISABLED)
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated users
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<AdminUserDTO>>> getAllUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get all users endpoint called - keyword: {}, status: {}, page: {}, size: {}", keyword, status, page, size);
        Page<AdminUserDTO> users = adminUserService.getAllUsers(keyword, status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    /**
     * Update user status (enable or disable account).
     * Admin can enable or disable user accounts to control access.
     *
     * @param userId the ID of the user to update
     * @param request the status update request containing the new status
     * @return ResponseEntity with ApiResponse containing the updated user
     */
    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUserStatus(
            @PathVariable("id") Long userId,
            @RequestBody StatusUpdateRequest request) {
        log.info("Update user status endpoint called - userId: {}, status: {}", userId, request.getStatus());
        AdminUserDTO user = adminUserService.updateUserStatus(userId, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
    }

    /**
     * Reset user password and generate a temporary password.
     * The temporary password should be communicated to the user through a secure channel.
     * User should be prompted to change the temporary password on next login.
     *
     * @param userId the ID of the user whose password to reset
     * @return ResponseEntity with ApiResponse containing the temporary password
     */
    @PostMapping("/users/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PasswordResetResponse>> resetUserPassword(
            @PathVariable("id") Long userId) {
        log.info("Reset user password endpoint called - userId: {}", userId);
        String tempPassword = adminUserService.resetUserPassword(userId);
        PasswordResetResponse response = PasswordResetResponse.builder()
                .tempPassword(tempPassword)
                .message("Temporary password generated. User should change it on next login.")
                .build();
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", response));
    }

}
