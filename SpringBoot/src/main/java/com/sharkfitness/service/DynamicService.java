package com.sharkfitness.service;

import com.sharkfitness.dto.DynamicCreateRequest;
import com.sharkfitness.vo.DynamicVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for dynamic post operations
 */
public interface DynamicService {
    
    /**
     * Create a new dynamic post
     * @param userId the user ID creating the post
     * @param request the dynamic creation request
     * @return created DynamicVO
     */
    DynamicVO create(Long userId, DynamicCreateRequest request);
    
    /**
     * Update a dynamic post
     * @param dynamicId the dynamic ID
     * @param userId the user ID updating the post
     * @param request the dynamic update request
     * @return updated DynamicVO
     */
    DynamicVO update(Long dynamicId, Long userId, DynamicCreateRequest request);
    
    /**
     * Delete a dynamic post
     * @param dynamicId the dynamic ID
     * @param userId the user ID deleting the post
     */
    void delete(Long dynamicId, Long userId);
    
    /**
     * Find all dynamic posts with pagination
     * @param pageable pagination information
     * @return Page of DynamicVO
     */
    Page<DynamicVO> findAll(Pageable pageable);
    
    /**
     * Find dynamic post by ID
     * @param dynamicId the dynamic ID
     * @return DynamicVO
     */
    DynamicVO findById(Long dynamicId);
}
