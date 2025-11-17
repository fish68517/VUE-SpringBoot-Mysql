package com.sharkfitness.service.impl;

import com.sharkfitness.dto.DynamicCreateRequest;
import com.sharkfitness.entity.Dynamic;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.DynamicRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.DynamicService;
import com.sharkfitness.vo.DynamicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of dynamic post service
 */
@Service
@RequiredArgsConstructor
public class DynamicServiceImpl implements DynamicService {
    
    private final DynamicRepository dynamicRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public DynamicVO create(Long userId, DynamicCreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Dynamic dynamic = new Dynamic();
        dynamic.setContent(request.getContent());
        dynamic.setImageUrls(request.getImageUrls());
        dynamic.setUser(user);
        dynamic.setStatus("approved");  // Set initial status to approved for regular posts
        dynamic.setLikeCount(0);
        dynamic.setCommentCount(0);
        
        dynamic = dynamicRepository.save(dynamic);
        
        return convertToVO(dynamic);
    }
    
    @Override
    @Transactional
    public DynamicVO update(Long dynamicId, Long userId, DynamicCreateRequest request) {
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        // Check if user is the owner of the post
        if (!dynamic.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }
        
        dynamic.setContent(request.getContent());
        dynamic.setImageUrls(request.getImageUrls());
        
        dynamic = dynamicRepository.save(dynamic);
        
        return convertToVO(dynamic);
    }
    
    @Override
    @Transactional
    public void delete(Long dynamicId, Long userId) {
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        // Check if user is the owner of the post
        if (!dynamic.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this post");
        }
        
        dynamicRepository.delete(dynamic);
    }
    
    @Override
    public Page<DynamicVO> findAll(Pageable pageable) {
        Page<Dynamic> dynamics = dynamicRepository.findAllByOrderByCreatedAtDesc(pageable);
        return dynamics.map(this::convertToVO);
    }
    
    @Override
    public DynamicVO findById(Long dynamicId) {
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        return convertToVO(dynamic);
    }
    
    /**
     * Convert Dynamic entity to DynamicVO
     */
    private DynamicVO convertToVO(Dynamic dynamic) {
        return new DynamicVO(
            dynamic.getId(),
            dynamic.getContent(),
            dynamic.getImageUrls(),
            dynamic.getUser().getId(),
            dynamic.getUser().getUsername(),
            dynamic.getUser().getAvatar(),
            dynamic.getLikeCount(),
            dynamic.getCommentCount(),
            dynamic.getStatus(),
            dynamic.getCreatedAt(),
            dynamic.getUpdatedAt()
        );
    }
}
