package com.sharkfitness.service;

import com.sharkfitness.vo.ResourceVO;

import java.util.List;

/**
 * Service interface for collection management
 */
public interface CollectionService {
    
    /**
     * Add a resource to user's collection
     */
    void addToCollection(Long userId, Long resourceId);
    
    /**
     * Remove a resource from user's collection
     */
    void removeFromCollection(Long userId, Long resourceId);
    
    /**
     * Get all resources in user's collection
     */
    List<ResourceVO> getUserCollections(Long userId);
    
    /**
     * Check if a resource is in user's collection
     */
    boolean isInCollection(Long userId, Long resourceId);
}
