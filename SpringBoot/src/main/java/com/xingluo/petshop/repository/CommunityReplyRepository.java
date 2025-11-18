package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.CommunityReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社区评论 Repository
 */
@Repository
public interface CommunityReplyRepository extends JpaRepository<CommunityReply, Long> {
    
    /**
     * 查询帖子的所有评论
     */
    List<CommunityReply> findByPostIdOrderByCreateTimeAsc(Long postId);
}
