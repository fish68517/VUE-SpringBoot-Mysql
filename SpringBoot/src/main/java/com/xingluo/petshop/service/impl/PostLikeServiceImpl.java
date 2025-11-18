package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.PostLike;
import com.xingluo.petshop.repository.CommunityPostRepository;
import com.xingluo.petshop.repository.PostLikeRepository;
import com.xingluo.petshop.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 帖子点赞服务实现类
 */
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {
    
    private final PostLikeRepository postLikeRepository;
    private final CommunityPostRepository communityPostRepository;
    
    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);
        
        if (existingLike.isPresent()) {
            // 已点赞，取消点赞
            postLikeRepository.delete(existingLike.get());
            communityPostRepository.decrementLikes(postId);
            return false;
        } else {
            // 未点赞，添加点赞
            PostLike like = new PostLike();
            like.setPostId(postId);
            like.setUserId(userId);
            postLikeRepository.save(like);
            communityPostRepository.incrementLikes(postId);
            return true;
        }
    }
    
    @Override
    public boolean isLiked(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }
}
