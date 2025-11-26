package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateLikeRequest;
import com.travelMemory.dto.LikeResponse;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class LikeController {

    private final LikeService likeService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new like
     * @param request the create like request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with LikeResponse
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LikeResponse>> createLike(
            @Valid @RequestBody CreateLikeRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Create like
            LikeResponse response = likeService.createLike(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to create like: " + e.getMessage()));
        }
    }

    /**
     * Delete a like
     * @param likeId the like ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{likeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteLike(
            @PathVariable Long likeId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete like
            likeService.deleteLike(likeId, userId);
            return ResponseEntity.ok(ApiResponse.success("Like deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to delete like: " + e.getMessage()));
        }
    }

    /**
     * Delete a like by travel record ID
     * @param travelRecordId the travel record ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/travels/{travelRecordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteLikeByTravelRecord(
            @PathVariable Long travelRecordId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete like
            likeService.deleteLikeByTravelRecordAndUser(travelRecordId, userId);
            return ResponseEntity.ok(ApiResponse.success("Like deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to delete like: " + e.getMessage()));
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
