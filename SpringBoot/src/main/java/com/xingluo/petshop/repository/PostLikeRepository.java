package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 帖子点赞 Repository
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    /**
     * 查询用户是否点赞了某个帖子
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);
    
    /**
     * 检查用户是否点赞了某个帖子
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
