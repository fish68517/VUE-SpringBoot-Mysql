package com.sharkfitness.service;

import com.sharkfitness.dto.CommentRequest;
import com.sharkfitness.vo.CommentVO;

import java.util.List;

/**
 * Service interface for comment operations
 */
public interface CommentService {
    
    /**
     * Create a new comment
     * @param userId the user ID creating the comment
     * @param request the comment creation request
     * @return created CommentVO
     */
    CommentVO create(Long userId, CommentRequest request);
    
    /**
     * Delete a comment
     * @param commentId the comment ID
     * @param userId the user ID deleting the comment
     */
    void delete(Long commentId, Long userId);
    
    /**
     * Find all comments for a dynamic post
     * @param dynamicId the dynamic ID
     * @return List of CommentVO
     */
    List<CommentVO> findByDynamic(Long dynamicId);
}
