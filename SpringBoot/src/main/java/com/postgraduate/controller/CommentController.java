package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.CommentDTO;
import com.postgraduate.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for comment management endpoints.
 * Handles retrieving, creating, and replying to comments on schools.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * Get all comments for a specific school with pagination.
     * Only returns comments with NORMAL status (excludes deleted comments).
     * Default page size is 20, maximum page size is 100.
     *
     * @param schoolId the school ID
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated comments
     */
    @GetMapping("/schools/{id}/comments")
    public ResponseEntity<ApiResponse<Page<CommentDTO>>> getSchoolComments(
            @PathVariable("id") Long schoolId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Get school comments endpoint called for school id: {} - page: {}, size: {}", schoolId, page, size);
        Page<CommentDTO> comments = commentService.getSchoolComments(schoolId, page, size);
        return ResponseEntity.ok(ApiResponse.success("School comments retrieved successfully", comments));
    }

    /**
     * Create a new comment for a school.
     *
     * @param schoolId the school ID
     * @param content the comment content
     * @return ResponseEntity with ApiResponse containing the created comment
     */
    @PostMapping("/schools/{id}/comments")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CommentDTO>> createComment(
            @PathVariable("id") Long schoolId,
            @RequestParam String content) {
        log.info("Create comment endpoint called for school id: {}", schoolId);
        CommentDTO comment = commentService.createComment(schoolId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", comment));
    }

    /**
     * Create a reply to an existing comment.
     *
     * @param commentId the parent comment ID
     * @param content the reply content
     * @return ResponseEntity with ApiResponse containing the created reply
     */
    @PostMapping("/comments/{id}/reply")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CommentDTO>> createReply(
            @PathVariable("id") Long commentId,
            @RequestParam String content) {
        log.info("Create reply endpoint called for comment id: {}", commentId);
        CommentDTO reply = commentService.createReply(commentId, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reply created successfully", reply));
    }

    /**
     * Delete a comment by marking it as DELETED.
     * Only the comment author or an admin can delete a comment.
     *
     * @param commentId the comment ID to delete
     * @return ResponseEntity with ApiResponse indicating success
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("id") Long commentId) {
        log.info("Delete comment endpoint called for comment id: {}", commentId);
        commentService.deleteCommentWithAuthorization(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}
