package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.FeedbackDTO;
import com.postgraduate.dto.FeedbackReplyRequest;
import com.postgraduate.dto.FeedbackSubmitRequest;
import com.postgraduate.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for feedback management endpoints.
 * Handles feedback submission, retrieval for users, and admin feedback management.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * Submit new feedback from the current user.
     * Feedback type must be one of: BUG, SUGGESTION, DATA_ERROR.
     *
     * @param request the feedback submission request containing type and content
     * @return ResponseEntity with ApiResponse containing the submitted feedback
     */
    @PostMapping("/feedback")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackDTO>> submitFeedback(
            @RequestBody FeedbackSubmitRequest request) {
        log.info("Submit feedback endpoint called with type: {}", request.getType());
        FeedbackDTO feedback = feedbackService.submitFeedback(request.getType(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feedback submitted successfully", feedback));
    }

    /**
     * Get all feedback submitted by the current user with pagination.
     * Default page size is 20, maximum page size is 100.
     *
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated feedback
     */
    @GetMapping("/me/feedback")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FeedbackDTO>>> getUserFeedback(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get user feedback endpoint called - page: {}, size: {}", page, size);
        Page<FeedbackDTO> feedback = feedbackService.getUserFeedback(page, size);
        return ResponseEntity.ok(ApiResponse.success("User feedback retrieved successfully", feedback));
    }

    /**
     * Get all feedback with optional filtering by status and type for admin management.
     * Supports pagination and filtering.
     * Status values: NEW, PROCESSING, DONE
     * Type values: BUG, SUGGESTION, DATA_ERROR
     *
     * @param status the feedback status to filter by (optional)
     * @param type the feedback type to filter by (optional)
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated feedback
     */
    @GetMapping("/admin/feedback")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<FeedbackDTO>>> getAdminFeedback(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get admin feedback endpoint called - status: {}, type: {}, page: {}, size: {}", status, type, page, size);
        Page<FeedbackDTO> feedback = feedbackService.getAdminFeedback(status, type, page, size);
        return ResponseEntity.ok(ApiResponse.success("Admin feedback retrieved successfully", feedback));
    }

    /**
     * Reply to feedback and update its status.
     * Admin can update the feedback status and provide a reply message.
     *
     * @param feedbackId the ID of the feedback to reply to
     * @param request the reply request containing status and admin reply
     * @return ResponseEntity with ApiResponse containing the updated feedback
     */
    @PatchMapping("/admin/feedback/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackDTO>> replyToFeedback(
            @PathVariable("id") Long feedbackId,
            @RequestBody FeedbackReplyRequest request) {
        log.info("Reply to feedback endpoint called - feedbackId: {}, status: {}", feedbackId, request.getStatus());
        FeedbackDTO feedback = feedbackService.replyToFeedback(feedbackId, request.getStatus(), request.getAdminReply());
        return ResponseEntity.ok(ApiResponse.success("Feedback updated successfully", feedback));
    }
}
