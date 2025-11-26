package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateTravelRecordRequest;
import com.travelMemory.dto.TravelRecordResponse;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/travels")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Travel Records", description = "Endpoints for managing travel records and memories")
public class TravelController {

    private final TravelService travelService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new travel record
     * @param request the create travel record request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with TravelRecordResponse
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new travel record", description = "Create a new travel record with title, destination, dates, and description")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Travel record created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.travelMemory.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<TravelRecordResponse>> createTravelRecord(
            @Valid @RequestBody CreateTravelRecordRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Create travel record
            TravelRecordResponse response = travelService.createTravelRecord(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to create travel record: " + e.getMessage()));
        }
    }

    /**
     * Get user's travel records with pagination
     * @param page the page number (0-indexed)
     * @param size the page size
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with Page of TravelRecordResponse
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TravelRecordResponse>>> getUserTravelRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Create pageable with sorting by created_at descending
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            // Get user's travel records
            Page<TravelRecordResponse> records = travelService.getUserTravelRecords(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve travel records: " + e.getMessage()));
        }
    }

    /**
     * Get a travel record by ID
     * @param id the record ID
     * @param authHeader the Authorization header containing JWT token (optional)
     * @return ResponseEntity with TravelRecordResponse
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> getTravelRecordById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token if available
            Long userId = extractUserIdFromToken(authHeader);

            TravelRecordResponse response;
            if (userId != null) {
                // If user is authenticated, check permissions
                response = travelService.getTravelRecordByIdWithPermissionCheck(id, userId);
            } else {
                // If user is not authenticated, only allow public records
                response = travelService.getTravelRecordById(id);
                if (!response.getIsPublic()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error(403, "Access denied: This record is private"));
                }
            }
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied") || e.getMessage().contains("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve travel record: " + e.getMessage()));
        }
    }

    /**
     * Get public travel records with pagination (for social feed)
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return ResponseEntity with Page of TravelRecordResponse
     */
    @GetMapping("/public/feed")
    public ResponseEntity<ApiResponse<Page<TravelRecordResponse>>> getPublicTravelRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // Create pageable with sorting by created_at descending
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            // Get public travel records
            Page<TravelRecordResponse> records = travelService.getPublicTravelRecords(pageable);
            return ResponseEntity.ok(ApiResponse.success(records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve public travel records: " + e.getMessage()));
        }
    }

    /**
     * Update a travel record
     * @param id the record ID
     * @param request the update travel record request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with TravelRecordResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TravelRecordResponse>> updateTravelRecord(
            @PathVariable Long id,
            @Valid @RequestBody CreateTravelRecordRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Update travel record
            TravelRecordResponse response = travelService.updateTravelRecord(id, userId, request);
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
                    .body(ApiResponse.error(500, "Failed to update travel record: " + e.getMessage()));
        }
    }

    /**
     * Delete a travel record
     * @param id the record ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteTravelRecord(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete travel record
            travelService.deleteTravelRecord(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Travel record deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to delete travel record: " + e.getMessage()));
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
