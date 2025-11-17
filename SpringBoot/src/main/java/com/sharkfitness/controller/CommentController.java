package com.sharkfitness.controller;

import com.sharkfitness.dto.CommentRequest;
import com.sharkfitness.service.CommentService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for comment operations
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    /**
     * Get all comments for a dynamic post
     * @param dynamicId the dynamic post ID
     * @return API response with list of comments
     */
    @GetMapping("/dynamics/{dynamicId}/comments")
    public ApiResponse<List<CommentVO>> getCommentsByDynamic(@PathVariable Long dynamicId) {
        List<CommentVO> comments = commentService.findByDynamic(dynamicId);
        return ApiResponse.success(comments);
    }
    
    /**
     * Create a new comment
     * @param request HTTP request containing current user ID
     * @param commentRequest comment creation request
     * @return API response with created comment
     */
    @PostMapping("/comments")
    public ApiResponse<CommentVO> createComment(
            HttpServletRequest request,
            @Valid @RequestBody CommentRequest commentRequest) {
        Long userId = (Long) request.getAttribute("currentUserId");
        CommentVO comment = commentService.create(userId, commentRequest);
        return ApiResponse.success(comment);
    }
    
    /**
     * Delete a comment
     * @param id the comment ID
     * @param request HTTP request containing current user ID
     * @return API response with success message
     */
    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        commentService.delete(id, userId);
        return ApiResponse.success(null);
    }
}
