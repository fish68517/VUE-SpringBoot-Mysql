package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateMapFootprintRequest;
import com.travelMemory.dto.MapFootprintResponse;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.service.FootprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class FootprintController {

    private final FootprintService footprintService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Add a map footprint to a travel record
     * @param request the create map footprint request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with MapFootprintResponse
     */
    @PostMapping("/{travelId}/footprints")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<MapFootprintResponse>> addFootprint(
            @Valid @RequestBody CreateMapFootprintRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Add footprint
            MapFootprintResponse response = footprintService.addFootprint(userId, request);
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
                    .body(ApiResponse.error(500, "Failed to add footprint: " + e.getMessage()));
        }
    }

    /**
     * Get all map footprints for a travel record
     * @param travelId the travel record ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with List of MapFootprintResponse
     */
    @GetMapping("/{travelId}/footprints")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<MapFootprintResponse>>> getFootprints(
            @PathVariable Long travelId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Get footprints
            List<MapFootprintResponse> footprints = footprintService.getFootprintsByTravelRecord(travelId, userId);
            return ResponseEntity.ok(ApiResponse.success(footprints));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve footprints: " + e.getMessage()));
        }
    }

    /**
     * Delete a map footprint
     * @param footprintId the footprint ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/footprints/{footprintId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteFootprint(
            @PathVariable Long footprintId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete footprint
            footprintService.deleteFootprint(footprintId, userId);
            return ResponseEntity.ok(ApiResponse.success("Map footprint deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to delete footprint: " + e.getMessage()));
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
