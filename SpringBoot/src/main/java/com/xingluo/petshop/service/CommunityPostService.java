package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.CommunityPost;
import org.springframework.data.domain.Page;

/**
 * 社区帖子服务接口
 */
public interface CommunityPostService {
    
    /**
     * 发布帖子
     */
    CommunityPost createPost(Long userId, String title, String content, String images);
    
    /**
     * 查询帖子列表（分页，按时间倒序）
     */
    Page<CommunityPost> getPostList(Integer page, Integer size);
    
    /**
     * 查询帖子详情
     */
    CommunityPost getPostDetail(Long postId);
    
    /**
     * 删除帖子
     */
    void deletePost(Long postId, Long userId);
    
    /**
     * 增加浏览数
     */
    void incrementViews(Long postId);
    
    /**
     * 查询所有帖子列表（管理员，分页）
     */
    Page<CommunityPost> getAllPostList(Integer page, Integer size);
    
    /**
     * 删除帖子（管理员）
     */
    void deletePostByAdmin(Long postId);
}
