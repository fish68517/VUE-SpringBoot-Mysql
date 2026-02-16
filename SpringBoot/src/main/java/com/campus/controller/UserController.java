package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.ChangePasswordRequest;
import com.campus.dto.PageResponse;
import com.campus.dto.UpdateProfileRequest;
import com.campus.dto.UpdateUserStatusRequest;
import com.campus.dto.UserDTO;
import com.campus.service.UserService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * Handles user profile management endpoints
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User profile management endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get current user profile
     * 
     * @param authHeader Authorization header containing JWT token
     * @return ApiResponse with user profile information
     */
    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Retrieve the profile information of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUserProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Get profile endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        UserDTO userDTO = userService.getCurrentUserProfile(username);

        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", userDTO));
    }

    /**
     * Update user profile
     * 
     * @param authHeader Authorization header containing JWT token
     * @param request Update profile request
     * @return ApiResponse with updated user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update user profile information including nickname, avatar, phone, gender, and birthday")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserProfile(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Update profile endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        UserDTO userDTO = userService.updateUserProfile(username, request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userDTO));
    }

    /**
     * Change user password
     * 
     * @param authHeader Authorization header containing JWT token
     * @param request Change password request
     * @return ApiResponse with success message
     */
    @PutMapping("/password")
    @Operation(summary = "Change user password", description = "Change the password of the currently authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        log.info("Change password endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        userService.changePassword(username, request);

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    /**
     * Extract username from JWT token in Authorization header
     * 
     * @param authHeader Authorization header
     * @return Username extracted from token, or null if invalid
     */
    private String extractUsernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        // Validate token
        if (!jwtUtil.validateToken(token)) {
            return null;
        }

        return jwtUtil.extractUsername(token);
    }

    /**
     * Get all users with pagination (Admin only)
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Sort field (default: createdAt)
     * @param sortDirection Sort direction (ASC or DESC)
     * @return ApiResponse with paginated user list
     */
    @GetMapping
    @Operation(summary = "Get all users with pagination", description = "Retrieve all users with pagination support (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.info("Get all users endpoint called - page: {}, size: {}", page, size);

        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        PageResponse<UserDTO> pageResponse = userService.getAllUsers(pageable);

        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", pageResponse));
    }

    /**
     * Search users by keyword (Admin only)
     * 
     * @param keyword Search keyword (username or nickname)
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with filtered user list
     */
    @GetMapping("/search")
    @Operation(summary = "Search users by keyword", description = "Search users by username or nickname (Admin only)")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Search users endpoint called - keyword: {}, page: {}, size: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<UserDTO> pageResponse = userService.searchUsers(keyword, pageable);

        return ResponseEntity.ok(ApiResponse.success("Users found successfully", pageResponse));
    }

    /**
     * Get user details by ID (Admin only)
     * 
     * @param userId User ID
     * @return ApiResponse with user details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user details by ID", description = "Retrieve detailed information of a specific user (Admin only)")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable("id") Long userId) {
        log.info("Get user by ID endpoint called - user ID: {}", userId);

        UserDTO userDTO = userService.getUserById(userId);

        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userDTO));
    }

    /**
     * Update user account status (Admin only)
     * 
     * @param userId User ID
     * @param request Update status request
     * @return ApiResponse with updated user information
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Update user account status", description = "Enable or disable a user account (Admin only)")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserStatus(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("Update user status endpoint called - user ID: {}, new status: {}", userId, request.getStatus());

        UserDTO userDTO = userService.updateUserStatus(userId, request);

        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", userDTO));
    }

}
