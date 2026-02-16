package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ApiResponse;
import com.campus.dto.RegistrationDTO;
import com.campus.entity.User;
import com.campus.service.RegistrationService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Registration Controller
 * Handles activity registration endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/registrations")
@Tag(name = "Activity Registration", description = "Activity registration management endpoints")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register user for an activity
     * 
     * @param authHeader Authorization header containing JWT token
     * @param registrationDTO Registration data
     * @return ApiResponse with created registration
     */
    @PostMapping
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Register for an activity", description = "Register the current user for an activity")
    public ResponseEntity<ApiResponse<RegistrationDTO>> registerForActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody RegistrationDTO registrationDTO) {
        log.info("Register for activity endpoint called - activity ID: {}", registrationDTO.getActivityId());

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        RegistrationDTO createdRegistration = registrationService.registerForActivity(registrationDTO, username);

        return ResponseEntity.ok(ApiResponse.success("Registration successful", createdRegistration));
    }

    /**
     * Cancel registration for an activity
     * 
     * @param authHeader Authorization header containing JWT token
     * @param registrationId Registration ID
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{id}")
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Cancel registration", description = "Cancel registration for an activity")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long registrationId) {
        log.info("Cancel registration endpoint called - registration ID: {}", registrationId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        registrationService.cancelRegistration(registrationId, username);

        return ResponseEntity.ok(ApiResponse.success("Registration cancelled successfully", null));
    }

    /**
     * Get user's registrations
     * 
     * @param authHeader Authorization header containing JWT token
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated registrations
     */
    @GetMapping("/my")
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get my registrations", description = "Get the current user's activity registrations")
    public ResponseEntity<ApiResponse<Page<RegistrationDTO>>> getMyRegistrations(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get my registrations endpoint called - page: {}, size: {}", page, size);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        Page<RegistrationDTO> registrations = registrationService.getUserRegistrations(username, page, size);

        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved successfully", registrations));
    }

    /**
     * Get activity registrations (for organizer/admin)
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated registrations
     */
    @GetMapping
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get activity registrations", description = "Get registrations for an activity (Organizer/Admin only)")
    public ResponseEntity<ApiResponse<Page<RegistrationDTO>>> getActivityRegistrations(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get activity registrations endpoint called - activity ID: {}, page: {}, size: {}", activityId, page, size);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        Page<RegistrationDTO> registrations = registrationService.getActivityRegistrations(activityId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Registrations retrieved successfully", registrations));
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

}
