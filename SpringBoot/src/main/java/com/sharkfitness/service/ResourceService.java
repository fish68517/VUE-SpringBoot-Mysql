package com.sharkfitness.service;

import com.sharkfitness.dto.ResourceCreateRequest;
import com.sharkfitness.vo.ResourceVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for fitness resource management
 */
public interface ResourceService {
    
    /**
     * Create a new fitness resource
     */
    ResourceVO create(ResourceCreateRequest request, Long creatorId);
    
    /**
     * Update an existing fitness resource
     */
    ResourceVO update(Long id, ResourceCreateRequest request, Long currentUserId);
    
    /**
     * Delete a fitness resource
     */
    void delete(Long id, Long currentUserId);
    
    /**
     * Find all resources with pagination
     */
    Page<ResourceVO> findAll(Pageable pageable);
    
    /**
     * Find resources by content type with pagination
     */
    Page<ResourceVO> findByContentType(String contentType, Pageable pageable);
    
    /**
     * Find resource by ID
     */
    ResourceVO findById(Long id);
    
    /**
     * Increment view count for a resource
     */
    void incrementViewCount(Long id);
}
