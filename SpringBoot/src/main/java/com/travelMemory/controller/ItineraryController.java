package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateItineraryItemRequest;
import com.travelMemory.dto.ItineraryItemResponse;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.service.ItineraryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plans/{planId}/items")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new itinerary item for a travel plan
     * @param planId the plan ID
     * @param request the create itinerary item request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with ItineraryItemResponse
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ItineraryItemResponse>> createItineraryItem(
            @PathVariable Long planId,
            @Valid @RequestBody CreateItineraryItemRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Create itinerary item
            ItineraryItemResponse response = itineraryService.createItineraryItem(planId, userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to create itinerary item: " + e.getMessage()));
        }
    }

    /**
     * Get all itinerary items for a travel plan
     * @param planId the plan ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with List of ItineraryItemResponse
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ItineraryItemResponse>>> getItineraryItems(
            @PathVariable Long planId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Get itinerary items
            List<ItineraryItemResponse> items = itineraryService.getItineraryItems(planId, userId);
            return ResponseEntity.ok(ApiResponse.success(items));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve itinerary items: " + e.getMessage()));
        }
    }

    /**
     * Update an itinerary item
     * @param planId the plan ID
     * @param itemId the item ID
     * @param request the update itinerary item request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with ItineraryItemResponse
     */
    @PutMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ItineraryItemResponse>> updateItineraryItem(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            @Valid @RequestBody CreateItineraryItemRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Update itinerary item
            ItineraryItemResponse response = itineraryService.updateItineraryItem(itemId, planId, userId, request);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to update itinerary item: " + e.getMessage()));
        }
    }

    /**
     * Delete an itinerary item
     * @param planId the plan ID
     * @param itemId the item ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteItineraryItem(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete itinerary item
            itineraryService.deleteItineraryItem(itemId, planId, userId);
            return ResponseEntity.ok(ApiResponse.success("Itinerary item deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to delete itinerary item: " + e.getMessage()));
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
