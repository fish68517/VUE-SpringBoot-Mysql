package com.campus.controller;

import com.campus.annotation.RequireRole;
import com.campus.dto.ApiResponse;
import com.campus.dto.FeedbackDTO;
import com.campus.entity.User;
import com.campus.service.FeedbackService;
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
 * Feedback Controller
 * Handles feedback submission and retrieval endpoints
 */
@Slf4j
@RestController
@RequestMapping("/api/feedback")
@Tag(name = "Feedback", description = "Feedback management endpoints")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Submit feedback for an activity
     * 
     * @param authHeader Authorization header containing JWT token
     * @param feedbackDTO Feedback data
     * @return ApiResponse with created feedback
     */
    @PostMapping
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Submit feedback", description = "Submit feedback for an activity")
    public ResponseEntity<ApiResponse<FeedbackDTO>> submitFeedback(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody FeedbackDTO feedbackDTO) {
        log.info("Submit feedback endpoint called - activity ID: {}", feedbackDTO.getActivityId());

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        FeedbackDTO createdFeedback = feedbackService.submitFeedback(feedbackDTO, username);

        return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully", createdFeedback));
    }

    /**
     * Get feedback for an activity
     * 
     * @param activityId Activity ID
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated feedback
     */
    @GetMapping("/activity/{activityId}")
    @Operation(summary = "Get activity feedback", description = "Get all feedback for an activity")
    public ResponseEntity<ApiResponse<Page<FeedbackDTO>>> getActivityFeedback(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get activity feedback endpoint called - activity ID: {}, page: {}, size: {}", activityId, page, size);

        Page<FeedbackDTO> feedback = feedbackService.getActivityFeedback(activityId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Feedback retrieved successfully", feedback));
    }

    /**
     * Get user's feedback
     * 
     * @param authHeader Authorization header containing JWT token
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return ApiResponse with paginated feedback
     */
    @GetMapping("/my")
    @RequireRole({User.UserRole.USER, User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Get my feedback", description = "Get the current user's feedback submissions")
    public ResponseEntity<ApiResponse<Page<FeedbackDTO>>> getMyFeedback(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get my feedback endpoint called - page: {}, size: {}", page, size);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        Page<FeedbackDTO> feedback = feedbackService.getUserFeedback(username, page, size);

        return ResponseEntity.ok(ApiResponse.success("Feedback retrieved successfully", feedback));
    }

    /**
     * Get average rating for an activity
     * 
     * @param activityId Activity ID
     * @return ApiResponse with average rating
     */
    @GetMapping("/activity/{activityId}/average-rating")
    @Operation(summary = "Get average rating", description = "Get average rating for an activity")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(
            @PathVariable Long activityId) {
        log.info("Get average rating endpoint called - activity ID: {}", activityId);

        Double averageRating = feedbackService.getAverageRating(activityId);

        return ResponseEntity.ok(ApiResponse.success("Average rating retrieved successfully", averageRating));
    }

    /**
     * Reply to feedback (organizer only)
     * 
     * @param feedbackId Feedback ID
     * @param authHeader Authorization header containing JWT token
     * @param replyRequest Request body containing reply content
     * @return ApiResponse with updated feedback
     */
    @PutMapping("/{feedbackId}/reply")
    @RequireRole({User.UserRole.ORGANIZER, User.UserRole.ADMIN})
    @Operation(summary = "Reply to feedback", description = "Reply to feedback for an activity (organizer only)")
    public ResponseEntity<ApiResponse<FeedbackDTO>> replyToFeedback(
            @PathVariable Long feedbackId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ReplyRequest replyRequest) {
        log.info("Reply to feedback endpoint called - feedback ID: {}", feedbackId);

        // Extract username from JWT token
        String username = extractUsernameFromToken(authHeader);
        if (username == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid or missing token"));
        }

        FeedbackDTO updatedFeedback = feedbackService.replyToFeedback(feedbackId, replyRequest.getReplyContent(), username);

        return ResponseEntity.ok(ApiResponse.success("Reply submitted successfully", updatedFeedback));
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
     * Request body for reply
     */
    public static class ReplyRequest {
        private String replyContent;

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }
    }

}
