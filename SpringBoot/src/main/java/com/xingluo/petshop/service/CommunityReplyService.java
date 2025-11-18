package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.CommunityReply;

import java.util.List;

/**
 * 社区评论服务接口
 */
public interface CommunityReplyService {
    
    /**
     * 发布评论
     */
    CommunityReply createReply(Long postId, Long userId, String content);
    
    /**
     * 查询帖子的所有评论
     */
    List<CommunityReply> getPostReplies(Long postId);
    
    /**
     * 删除评论（管理员）
     */
    void deleteReply(Long replyId);
}
