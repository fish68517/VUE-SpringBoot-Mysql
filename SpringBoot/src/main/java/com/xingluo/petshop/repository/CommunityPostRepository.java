package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.CommunityPost;
import org.apache.ibatis.annotations.Delete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 社区帖子 Repository
 */
@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    
    /**
     * 查询正常状态的帖子列表（分页）
     */
    Page<CommunityPost> findByStatusOrderByCreateTimeDesc(Integer status, Pageable pageable);
    
    /**
     * 增加浏览数
     */
    @Modifying
    @Query("UPDATE CommunityPost p SET p.views = p.views + 1 WHERE p.id = :postId")
    void incrementViews(@Param("postId") Long postId);
    
    /**
     * 增加点赞数
     */
    @Modifying
    @Query("UPDATE CommunityPost p SET p.likes = p.likes + 1 WHERE p.id = :postId")
    void incrementLikes(@Param("postId") Long postId);
    
    /**
     * 减少点赞数
     */
    @Modifying
    @Query("UPDATE CommunityPost p SET p.likes = p.likes - 1 WHERE p.id = :postId")
    void decrementLikes(@Param("postId") Long postId);

    // 删除帖子
    @Modifying
    @Query("UPDATE CommunityPost p SET p.status = 1 WHERE p.id = :postId")
    void deletePost(@Param("postId") Long postId);

}
