package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.CommunityPost;
import com.xingluo.petshop.repository.CommunityPostRepository;
import com.xingluo.petshop.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 社区帖子服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {
    
    private final CommunityPostRepository communityPostRepository;
    
    @Override
    @Transactional
    public CommunityPost createPost(Long userId, String title, String content, String images) {
        CommunityPost post = new CommunityPost();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setImages(images);
        post.setStatus(0); // 0-正常
        post.setLikes(0);
        post.setViews(0);
        
        return communityPostRepository.save(post);
    }
    
    @Override
    public Page<CommunityPost> getPostList(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return communityPostRepository.findByStatusOrderByCreateTimeDesc(0, pageable);
    }
    
    @Override
    public CommunityPost getPostDetail(Long postId) {
        return communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
    }
    
    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        // 验证是否是帖子作者
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该帖子");
        }
        
        // 软删除：更新状态为已删除
        post.setStatus(1);
        communityPostRepository.save(post);
    }
    
    @Override
    @Transactional
    public void incrementViews(Long postId) {
        communityPostRepository.incrementViews(postId);
    }
    
    @Override
    public Page<CommunityPost> getAllPostList(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return communityPostRepository.findAll(pageable);
    }
    
    @Override
    @Transactional
    public void deletePostByAdmin(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        
        // 管理员删除：软删除，更新状态为已删除
        post.setStatus(1);
        communityPostRepository.save(post);
    }
}
