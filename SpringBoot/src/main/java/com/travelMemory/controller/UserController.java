package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.UpdateUserRequest;
import com.travelMemory.dto.UserResponse;
import com.travelMemory.dto.UserStatisticsResponse;
import com.travelMemory.exception.AccessDeniedException;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.security.PermissionService;
import com.travelMemory.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get user information by ID
     * @param id the user ID
     * @return ResponseEntity with UserResponse containing user information
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse userResponse = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve user: " + e.getMessage()));
        }
    }

    /**
     * Update user information
     * @param id the user ID
     * @param updateRequest the update request containing new user information
     * @return ResponseEntity with UserResponse containing updated user information
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        try {
            // Verify that the current user is updating their own profile
            permissionService.verifyResourceOwner(id);
            
            UserResponse userResponse = userService.updateUser(id, updateRequest);
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to update user: " + e.getMessage()));
        }
    }

    /**
     * Get user statistics for dashboard
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with UserStatisticsResponse containing user statistics
     */
    @GetMapping("/statistics/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            UserStatisticsResponse statistics = userService.getUserStatistics(userId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve statistics: " + e.getMessage()));
        }
    }

    /**
     * Extract user ID from JWT token in Authorization header
     * @param authHeader the Authorization header
     * @return the user ID or null if token is invalid
     */
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        try {
            String userIdStr = jwtTokenProvider.getUserIdFromToken(token);
            return userIdStr != null ? Long.parseLong(userIdStr) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
