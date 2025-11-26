package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CreateTravelPlanRequest;
import com.travelMemory.dto.TravelPlanResponse;
import com.travelMemory.security.JwtTokenProvider;
import com.travelMemory.service.PlanService;
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
@RequestMapping("/plans")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PlanController {

    private final PlanService planService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Create a new travel plan
     * @param request the create travel plan request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with TravelPlanResponse
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TravelPlanResponse>> createTravelPlan(
            @Valid @RequestBody CreateTravelPlanRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Create travel plan
            TravelPlanResponse response = planService.createTravelPlan(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to create travel plan: " + e.getMessage()));
        }
    }

    /**
     * Get user's travel plans with pagination
     * @param page the page number (0-indexed)
     * @param size the page size
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with Page of TravelPlanResponse
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<TravelPlanResponse>>> getUserTravelPlans(
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

            // Get user's travel plans
            Page<TravelPlanResponse> plans = planService.getUserTravelPlans(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(plans));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve travel plans: " + e.getMessage()));
        }
    }

    /**
     * Get a travel plan by ID
     * @param id the plan ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with TravelPlanResponse
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TravelPlanResponse>> getTravelPlanById(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Get travel plan with ownership verification
            TravelPlanResponse response = planService.getTravelPlanByIdAndUserId(id, userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to retrieve travel plan: " + e.getMessage()));
        }
    }

    /**
     * Update a travel plan
     * @param id the plan ID
     * @param request the update travel plan request
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with TravelPlanResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TravelPlanResponse>> updateTravelPlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateTravelPlanRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Update travel plan
            TravelPlanResponse response = planService.updateTravelPlan(id, userId, request);
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
                    .body(ApiResponse.error(500, "Failed to update travel plan: " + e.getMessage()));
        }
    }

    /**
     * Delete a travel plan
     * @param id the plan ID
     * @param authHeader the Authorization header containing JWT token
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteTravelPlan(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Extract user ID from JWT token
            Long userId = extractUserIdFromToken(authHeader);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(401, "Unauthorized"));
            }

            // Delete travel plan
            planService.deleteTravelPlan(id, userId);
            return ResponseEntity.ok(ApiResponse.success("Travel plan deleted successfully", null));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to delete travel plan: " + e.getMessage()));
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
