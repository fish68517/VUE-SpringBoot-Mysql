package com.xingluo.petshop.service;

/**
 * 帖子点赞服务接口
 */
public interface PostLikeService {
    
    /**
     * 点赞/取消点赞
     * @return true-点赞成功，false-取消点赞成功
     */
    boolean toggleLike(Long postId, Long userId);
    
    /**
     * 查询用户是否点赞了某个帖子
     */
    boolean isLiked(Long postId, Long userId);
}
