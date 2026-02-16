package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ApiResponse;
import com.campus.dto.FavoriteDTO;
import com.campus.entity.User;
import com.campus.service.FavoriteService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Favorite Controller
 * Handles favorite management endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/activities")
@Tag(name = "Favorite Management", description = "Activity favorite endpoints")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Add activity to user's favorites
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @return ApiResponse with created favorite
     */
    @PostMapping("/{id}/favorite")
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Add activity to favorites", description = "Add an activity to user's favorite list")
    public ResponseEntity<ApiResponse<FavoriteDTO>> addFavorite(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long activityId) {
        log.info("Add favorite endpoint called - activity ID: {}", activityId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        FavoriteDTO favorite = favoriteService.addFavorite(activityId, username);

        return ResponseEntity.ok(ApiResponse.success("Activity added to favorites successfully", favorite));
    }

    /**
     * Remove activity from user's favorites
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @return ApiResponse with success message
     */
    @DeleteMapping("/{id}/favorite")
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Remove activity from favorites", description = "Remove an activity from user's favorite list")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long activityId) {
        log.info("Remove favorite endpoint called - activity ID: {}", activityId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        favoriteService.removeFavorite(activityId, username);

        return ResponseEntity.ok(ApiResponse.success("Activity removed from favorites successfully", null));
    }

    /**
     * Get user's favorite activities list
     * 
     * @param authHeader Authorization header containing JWT token
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated favorite list
     */
    @GetMapping("/favorites")
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get favorite activities", description = "Retrieve user's favorite activities list with pagination")
    public ResponseEntity<ApiResponse<Page<FavoriteDTO>>> getFavorites(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get favorites endpoint called - page: {}, size: {}", page, size);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        Page<FavoriteDTO> favorites = favoriteService.getFavorites(username, page, size);

        return ResponseEntity.ok(ApiResponse.success("Favorites retrieved successfully", favorites));
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
