package com.sharkfitness.service;

/**
 * Service interface for like operations
 */
public interface LikeService {
    
    /**
     * Like a dynamic post
     * @param userId the user ID
     * @param dynamicId the dynamic ID
     */
    void likePost(Long userId, Long dynamicId);
    
    /**
     * Unlike a dynamic post
     * @param userId the user ID
     * @param dynamicId the dynamic ID
     */
    void unlikePost(Long userId, Long dynamicId);
}
