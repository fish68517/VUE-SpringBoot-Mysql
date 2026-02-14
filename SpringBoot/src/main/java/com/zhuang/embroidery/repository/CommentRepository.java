package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问层接口
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据作品ID查询评论列表（分页）
     *
     * @param artworkId 作品ID
     * @param pageable 分页信息
     * @return 评论分页列表
     */
    Page<Comment> findByArtworkId(Long artworkId, Pageable pageable);

    /**
     * 根据话题ID查询评论列表（分页）
     *
     * @param topicId 话题ID
     * @param pageable 分页信息
     * @return 评论分页列表
     */
    Page<Comment> findByTopicId(Long topicId, Pageable pageable);

    /**
     * 根据用户ID查询评论列表
     *
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> findByUserId(Long userId);

    /**
     * 根据父评论ID查询回复列表
     *
     * @param parentId 父评论ID
     * @return 回复列表
     */
    List<Comment> findByParentId(Long parentId);

    /**
     * 根据作品ID统计评论数
     *
     * @param artworkId 作品ID
     * @return 评论数
     */
    Long countByArtworkId(Long artworkId);

    /**
     * 根据话题ID统计评论数
     *
     * @param topicId 话题ID
     * @return 评论数
     */
    Long countByTopicId(Long topicId);
}
