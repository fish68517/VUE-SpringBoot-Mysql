package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ActivityDTO;
import com.campus.dto.ApiResponse;
import com.campus.entity.User;
import com.campus.service.ActivityService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Activity Controller
 * Handles activity management endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activity Management", description = "Activity creation, editing, and deletion endpoints")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create a new activity
     * Only organizers can create activities
     * Activity is saved as PENDING_AUDIT status
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityDTO Activity data
     * @return ApiResponse with created activity
     */
    @PostMapping
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Create a new activity", description = "Create a new activity (Organizer only). Activity will be saved as PENDING_AUDIT status")
    public ResponseEntity<ApiResponse<ActivityDTO>> createActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ActivityDTO activityDTO) {
        log.info("Create activity endpoint called");

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        ActivityDTO createdActivity = activityService.createActivity(activityDTO, username);

        return ResponseEntity.ok(ApiResponse.success("Activity created successfully", createdActivity));
    }

    /**
     * Update an existing activity
     * Only the organizer who created the activity can edit it
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @param activityDTO Updated activity data
     * @return ApiResponse with updated activity
     */
    @PutMapping("/{id}")
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Update an activity", description = "Update an existing activity (Organizer only)")
    public ResponseEntity<ApiResponse<ActivityDTO>> updateActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long activityId,
            @Valid @RequestBody ActivityDTO activityDTO) {
        log.info("Update activity endpoint called - activity ID: {}", activityId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        ActivityDTO updatedActivity = activityService.updateActivity(activityId, activityDTO, username);

        return ResponseEntity.ok(ApiResponse.success("Activity updated successfully", updatedActivity));
    }

    /**
     * Delete an activity
     * Only the organizer who created the activity can delete it
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{id}")
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Delete an activity", description = "Delete an activity (Organizer only)")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long activityId) {
        log.info("Delete activity endpoint called - activity ID: {}", activityId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        activityService.deleteActivity(activityId, username);

        return ResponseEntity.ok(ApiResponse.success("Activity deleted successfully", null));
    }

    /**
     * Get activity details by ID
     * 
     * @param activityId Activity ID
     * @return ApiResponse with activity details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get activity details", description = "Retrieve detailed information of a specific activity")
    public ResponseEntity<ApiResponse<ActivityDTO>> getActivityById(@PathVariable("id") Long activityId) {
        log.info("Get activity by ID endpoint called - activity ID: {}", activityId);

        ActivityDTO activityDTO = activityService.getActivityById(activityId);

        return ResponseEntity.ok(ApiResponse.success("Activity retrieved successfully", activityDTO));
    }

    /**
     * Get activity list with filtering and pagination
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param type Activity type filter (optional)
     * @param sortBy Sort field: "startTime", "popularity", "recent"
     * @param startDate Start date filter (optional, format: yyyy-MM-dd'T'HH:mm:ss)
     * @param endDate End date filter (optional, format: yyyy-MM-dd'T'HH:mm:ss)
     * @return ApiResponse with paginated activity list
     */
    @GetMapping
    @Operation(summary = "Get activity list", description = "Retrieve activity list with filtering and pagination")
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getActivityList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Get activity list endpoint called - page: {}, size: {}, type: {}, sortBy: {}", page, size, type, sortBy);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // Parse date parameters if provided
        if (startDate != null && !startDate.isEmpty()) {
            try {
                startDateTime = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                log.warn("Invalid startDate format: {}", startDate);
            }
        }

        if (endDate != null && !endDate.isEmpty()) {
            try {
                endDateTime = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME);
            } catch (Exception e) {
                log.warn("Invalid endDate format: {}", endDate);
            }
        }

        Page<ActivityDTO> activityPage = activityService.getActivityList(page, size, type, sortBy, startDateTime, endDateTime);

        return ResponseEntity.ok(ApiResponse.success("Activity list retrieved successfully", activityPage));
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
