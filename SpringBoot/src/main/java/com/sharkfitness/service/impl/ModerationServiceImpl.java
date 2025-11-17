package com.sharkfitness.service.impl;

import com.sharkfitness.entity.Dynamic;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.repository.DynamicRepository;
import com.sharkfitness.service.ModerationService;
import com.sharkfitness.vo.DynamicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of moderation service
 */
@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {
    
    private final DynamicRepository dynamicRepository;
    
    @Override
    public Page<DynamicVO> getModerationQueue(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Dynamic> dynamics = dynamicRepository.findByStatusOrderByCreatedAtDesc("pending", pageable);
        
        return dynamics.map(this::convertToVO);
    }
    
    @Override
    @Transactional
    public DynamicVO approveContent(Long dynamicId) {
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        dynamic.setStatus("approved");
        dynamic = dynamicRepository.save(dynamic);
        
        return convertToVO(dynamic);
    }
    
    @Override
    @Transactional
    public DynamicVO rejectContent(Long dynamicId, String reason) {
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        dynamic.setStatus("rejected");
        // Note: The reason could be stored in a separate field if needed
        // For now, we just update the status
        dynamic = dynamicRepository.save(dynamic);
        
        return convertToVO(dynamic);
    }
    
    @Override
    public Page<DynamicVO> getModerationHistory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Get all dynamics ordered by created date (includes approved and rejected)
        Page<Dynamic> dynamics = dynamicRepository.findAll(pageable);
        
        return dynamics.map(this::convertToVO);
    }
    
    /**
     * Convert Dynamic entity to DynamicVO
     */
    private DynamicVO convertToVO(Dynamic dynamic) {
        DynamicVO vo = new DynamicVO();
        vo.setId(dynamic.getId());
        vo.setContent(dynamic.getContent());
        vo.setImageUrls(dynamic.getImageUrls());
        vo.setUserId(dynamic.getUser().getId());
        vo.setUsername(dynamic.getUser().getUsername());
        vo.setUserAvatar(dynamic.getUser().getAvatar());
        vo.setLikeCount(dynamic.getLikeCount());
        vo.setCommentCount(dynamic.getCommentCount());
        vo.setStatus(dynamic.getStatus());
        vo.setCreatedAt(dynamic.getCreatedAt());
        vo.setUpdatedAt(dynamic.getUpdatedAt());
        
        return vo;
    }
}
