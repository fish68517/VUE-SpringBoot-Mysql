package com.sharkfitness.service.impl;

import com.sharkfitness.entity.Dynamic;
import com.sharkfitness.entity.Like;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.repository.DynamicRepository;
import com.sharkfitness.repository.LikeRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of like service
 */
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    
    private final LikeRepository likeRepository;
    private final DynamicRepository dynamicRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void likePost(Long userId, Long dynamicId) {
        // Check if user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if dynamic exists
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        // Check if already liked
        if (likeRepository.existsByUserIdAndDynamicId(userId, dynamicId)) {
            throw new BusinessException(400, "You have already liked this post");
        }
        
        // Create like record
        Like like = new Like();
        like.setUser(user);
        like.setDynamic(dynamic);
        likeRepository.save(like);
        
        // Update like count
        dynamic.setLikeCount(dynamic.getLikeCount() + 1);
        dynamicRepository.save(dynamic);
    }
    
    @Override
    @Transactional
    public void unlikePost(Long userId, Long dynamicId) {
        // Check if dynamic exists
        Dynamic dynamic = dynamicRepository.findById(dynamicId)
            .orElseThrow(() -> new ResourceNotFoundException("Dynamic post not found"));
        
        // Find like record
        Like like = likeRepository.findByUserIdAndDynamicId(userId, dynamicId)
            .orElseThrow(() -> new BusinessException(400, "You have not liked this post"));
        
        // Delete like record
        likeRepository.delete(like);
        
        // Update like count
        dynamic.setLikeCount(Math.max(0, dynamic.getLikeCount() - 1));
        dynamicRepository.save(dynamic);
    }
}
