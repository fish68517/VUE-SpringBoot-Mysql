package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.CommunityReply;
import com.xingluo.petshop.repository.CommunityReplyRepository;
import com.xingluo.petshop.service.CommunityReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 社区评论服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommunityReplyServiceImpl implements CommunityReplyService {
    
    private final CommunityReplyRepository communityReplyRepository;
    
    @Override
    @Transactional
    public CommunityReply createReply(Long postId, Long userId, String content) {
        CommunityReply reply = new CommunityReply();
        reply.setPostId(postId);
        reply.setUserId(userId);
        reply.setContent(content);
        
        return communityReplyRepository.save(reply);
    }
    
    @Override
    public List<CommunityReply> getPostReplies(Long postId) {
        return communityReplyRepository.findByPostIdOrderByCreateTimeAsc(postId);
    }
    
    @Override
    @Transactional
    public void deleteReply(Long replyId) {
        CommunityReply reply = communityReplyRepository.findById(replyId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // 管理员删除评论：物理删除
        communityReplyRepository.delete(reply);
    }
}
