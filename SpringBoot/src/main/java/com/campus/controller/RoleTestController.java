package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ApiResponse;
import com.campus.entity.User.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Role Test Controller
 * Demonstrates role-based access control using @RequireRole annotation
 */
@Slf4j
@RestController
@RequestMapping("/test")
@Tag(name = "Role Test", description = "Test endpoints for role-based access control")
public class RoleTestController {

    /**
     * Test endpoint accessible only by ADMIN role
     */
    @GetMapping("/admin-only")
    @RequireRole(UserRole.ADMIN)
    @Operation(summary = "Admin only endpoint", description = "This endpoint is accessible only by users with ADMIN role")
    public ResponseEntity<ApiResponse<String>> adminOnly() {
        log.info("Admin only endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("Admin access granted", "This is admin-only content"));
    }

    /**
     * Test endpoint accessible only by ORGANIZER role
     */
    @GetMapping("/organizer-only")
    @RequireRole(UserRole.ORGANIZER)
    @Operation(summary = "Organizer only endpoint", description = "This endpoint is accessible only by users with ORGANIZER role")
    public ResponseEntity<ApiResponse<String>> organizerOnly() {
        log.info("Organizer only endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("Organizer access granted", "This is organizer-only content"));
    }

    /**
     * Test endpoint accessible only by USER role
     */
    @GetMapping("/user-only")
    @RequireRole(UserRole.USER)
    @Operation(summary = "User only endpoint", description = "This endpoint is accessible only by users with USER role")
    public ResponseEntity<ApiResponse<String>> userOnly() {
        log.info("User only endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("User access granted", "This is user-only content"));
    }

    /**
     * Test endpoint accessible by ADMIN or ORGANIZER roles
     */
    @GetMapping("/admin-or-organizer")
    @RequireRole({UserRole.ADMIN, UserRole.ORGANIZER})
    @Operation(summary = "Admin or Organizer endpoint", description = "This endpoint is accessible by users with ADMIN or ORGANIZER role")
    public ResponseEntity<ApiResponse<String>> adminOrOrganizer() {
        log.info("Admin or Organizer endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("Access granted", "This is admin or organizer content"));
    }

    /**
     * Test endpoint accessible by all authenticated users
     */
    @GetMapping("/authenticated-users")
    @Operation(summary = "Authenticated users endpoint", description = "This endpoint is accessible by all authenticated users")
    public ResponseEntity<ApiResponse<String>> authenticatedUsers() {
        log.info("Authenticated users endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("Access granted", "This is content for all authenticated users"));
    }

}
