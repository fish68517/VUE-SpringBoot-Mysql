package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ActivityDTO;
import com.campus.dto.ApiResponse;
import com.campus.dto.AuditLogDTO;
import com.campus.dto.FundProofDTO;
import com.campus.entity.User;
import com.campus.service.AuditService;
import com.campus.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Audit Controller
 * Handles audit operations for activities and fund proofs
 */
@Slf4j
@RestController
@RequestMapping("/api/audit")
@Tag(name = "Audit Management", description = "Activity and fund proof audit endpoints")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get pending audit activities list
     * Only admins can access this
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated pending audit activities
     */
    @GetMapping("/activities")
    @RequireRole(User.UserRole.ADMIN)
    @Operation(summary = "Get pending audit activities", description = "Retrieve list of activities pending audit (Admin only)")
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getPendingAuditActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get pending audit activities endpoint called - page: {}, size: {}", page, size);

        Page<ActivityDTO> activityPage = auditService.getPendingAuditActivities(page, size);

        return ResponseEntity.ok(ApiResponse.success("Pending audit activities retrieved successfully", activityPage));
    }

    /**
     * Audit an activity (approve or reject)
     * Only admins can perform this operation
     * 
     * @param authHeader Authorization header containing JWT token
     * @param activityId Activity ID
     * @param auditRequest Audit request containing approval status and reason
     * @return ApiResponse with updated activity
     */
    @PutMapping("/activities/{id}")
    @RequireRole(User.UserRole.ADMIN)
    @Operation(summary = "Audit an activity", description = "Approve or reject an activity (Admin only)")
    public ResponseEntity<ApiResponse<ActivityDTO>> auditActivity(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long activityId,
            @RequestBody AuditRequest auditRequest) {
        log.info("Audit activity endpoint called - activity ID: {}", activityId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        // Validate audit request
        if (auditRequest == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Audit request is required"));
        }

        ActivityDTO auditedActivity = auditService.auditActivity(
                activityId,
                auditRequest.isApproved(),
                auditRequest.getReason(),
                username
        );

        return ResponseEntity.ok(ApiResponse.success("Activity audited successfully", auditedActivity));
    }

    /**
     * Get audit logs for an activity
     * 
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated audit logs
     */
    @GetMapping("/activities/{id}/logs")
    @Operation(summary = "Get activity audit logs", description = "Retrieve audit logs for a specific activity")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getActivityAuditLogs(
            @PathVariable("id") Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get activity audit logs endpoint called - activity ID: {}", activityId);

        Page<AuditLogDTO> auditLogPage = auditService.getActivityAuditLogs(activityId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Activity audit logs retrieved successfully", auditLogPage));
    }

    /**
     * Get pending audit fund proofs list
     * Only admins can access this
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated pending audit fund proofs
     */
    @GetMapping("/crowdfunding")
    @RequireRole(User.UserRole.ADMIN)
    @Operation(summary = "Get pending audit fund proofs", description = "Retrieve list of fund proofs pending audit (Admin only)")
    public ResponseEntity<ApiResponse<Page<FundProofDTO>>> getPendingAuditFundProofs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get pending audit fund proofs endpoint called - page: {}, size: {}", page, size);

        Page<FundProofDTO> fundProofPage = auditService.getPendingAuditFundProofs(page, size);

        return ResponseEntity.ok(ApiResponse.success("Pending audit fund proofs retrieved successfully", fundProofPage));
    }

    /**
     * Audit a fund proof (approve or reject)
     * Only admins can perform this operation
     * 
     * @param authHeader Authorization header containing JWT token
     * @param fundProofId Fund proof ID
     * @param auditRequest Audit request containing approval status and reason
     * @return ApiResponse with updated fund proof
     */
    @PutMapping("/crowdfunding/{id}")
    @RequireRole(User.UserRole.ADMIN)
    @Operation(summary = "Audit a fund proof", description = "Approve or reject a fund proof (Admin only)")
    public ResponseEntity<ApiResponse<FundProofDTO>> auditFundProof(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("id") Long fundProofId,
            @RequestBody AuditRequest auditRequest) {
        log.info("Audit fund proof endpoint called - fund proof ID: {}", fundProofId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        // Validate audit request
        if (auditRequest == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Audit request is required"));
        }

        FundProofDTO auditedFundProof = auditService.auditFundProof(
                fundProofId,
                auditRequest.isApproved(),
                auditRequest.getReason(),
                username
        );

        return ResponseEntity.ok(ApiResponse.success("Fund proof audited successfully", auditedFundProof));
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

    /**
     * Audit Request DTO
     */
    public static class AuditRequest {
        private boolean approved;
        private String reason;

        public AuditRequest() {
        }

        public AuditRequest(boolean approved, String reason) {
            this.approved = approved;
            this.reason = reason;
        }

        public boolean isApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

}
