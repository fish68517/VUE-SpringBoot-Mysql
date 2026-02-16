package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ApiResponse;
import com.campus.dto.FundProofDTO;
import com.campus.entity.User;
import com.campus.service.FundProofService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Fund Proof Controller
 * Handles fund proof submission and retrieval endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/fund-proofs")
@Tag(name = "Fund Proof", description = "Fund proof management endpoints")
public class FundProofController {

    @Autowired
    private FundProofService fundProofService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Submit fund proof for an activity
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @param file Proof file
     * @param description Usage description
     * @return ApiResponse with created fund proof
     */
    @PostMapping
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Submit fund proof", description = "Submit fund usage proof for an activity")
    public ResponseEntity<ApiResponse<FundProofDTO>> submitFundProof(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam Long activityId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String description) {
        log.info("Submit fund proof endpoint called - activity ID: {}", activityId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        FundProofDTO createdProof = fundProofService.submitFundProof(activityId, file, description, username);

        return ResponseEntity.ok(ApiResponse.success("Fund proof submitted successfully", createdProof));
    }

    /**
     * Get fund proofs for an activity
     * 
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated fund proofs
     */
    @GetMapping("/activity/{activityId}")
    @Operation(summary = "Get activity fund proofs", description = "Get all fund proofs for an activity")
    public ResponseEntity<ApiResponse<Page<FundProofDTO>>> getActivityFundProofs(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get activity fund proofs endpoint called - activity ID: {}, page: {}, size: {}", activityId, page, size);

        Page<FundProofDTO> proofs = fundProofService.getActivityFundProofs(activityId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Fund proofs retrieved successfully", proofs));
    }

    /**
     * Get fund proofs submitted by current organizer
     * 
     * @param authHeader Authorization header containing JWT token
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated fund proofs
     */
    @GetMapping("/my")
    @RequireRole({User.UserRole.ORGANIZER})
    @Operation(summary = "Get my fund proofs", description = "Get all fund proofs submitted by current organizer")
    public ResponseEntity<ApiResponse<Page<FundProofDTO>>> getMyFundProofs(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get my fund proofs endpoint called - page: {}, size: {}", page, size);

        // Extract user ID from JWT token
        Long userId = extractUserIdFromToken(authHeader);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        Page<FundProofDTO> proofs = fundProofService.getOrganizerFundProofs(userId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Fund proofs retrieved successfully", proofs));
    }

    /**
     * Get fund proof by ID
     * 
     * @param id Fund proof ID
     * @return ApiResponse with fund proof
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get fund proof", description = "Get fund proof by ID")
    public ResponseEntity<ApiResponse<FundProofDTO>> getFundProofById(@PathVariable Long id) {
        log.info("Get fund proof endpoint called - ID: {}", id);

        FundProofDTO proof = fundProofService.getFundProofById(id);

        return ResponseEntity.ok(ApiResponse.success("Fund proof retrieved successfully", proof));
    }

    /**
     * Extract username from JWT token
     * 
     * @param authHeader Authorization header
     * @return Username or null if invalid
     */
    private String extractUsernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }

    /**
     * Extract user ID from JWT token
     * 
     * @param authHeader Authorization header
     * @return User ID or null if invalid
     */
    private Long extractUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }

}
