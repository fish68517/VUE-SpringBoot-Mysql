package com.sharkfitness.service;

import com.sharkfitness.vo.DynamicVO;
import org.springframework.data.domain.Page;

/**
 * Service interface for content moderation operations
 */
public interface ModerationService {
    
    /**
     * Get moderation queue with pagination
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of dynamics pending moderation
     */
    Page<DynamicVO> getModerationQueue(int page, int size);
    
    /**
     * Approve a dynamic post
     * 
     * @param dynamicId Dynamic post ID
     * @return Updated dynamic
     */
    DynamicVO approveContent(Long dynamicId);
    
    /**
     * Reject a dynamic post
     * 
     * @param dynamicId Dynamic post ID
     * @param reason Rejection reason
     * @return Updated dynamic
     */
    DynamicVO rejectContent(Long dynamicId, String reason);
    
    /**
     * Get moderation history (all moderated content)
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @return Page of moderated dynamics
     */
    Page<DynamicVO> getModerationHistory(int page, int size);
}
