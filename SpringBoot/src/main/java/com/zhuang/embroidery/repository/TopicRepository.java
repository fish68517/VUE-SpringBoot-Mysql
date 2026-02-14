package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 话题数据访问层接口
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * 查询所有话题列表（分页）
     *
     * @param pageable 分页信息
     * @return 话题分页列表
     */
    Page<Topic> findAll(Pageable pageable);

    /**
     * 查询所有置顶话题
     *
     * @return 置顶话题列表
     */
    List<Topic> findByIsPinnedTrue();

    /**
     * 查询所有未置顶话题（分页）
     *
     * @param pageable 分页信息
     * @return 未置顶话题分页列表
     */
    Page<Topic> findByIsPinnedFalse(Pageable pageable);

    /**
     * 根据创建者ID查询话题列表
     *
     * @param createdBy 创建者ID
     * @return 话题列表
     */
    List<Topic> findByCreatedBy(Long createdBy);

    /**
     * 根据标题模糊查询话题列表（分页）
     *
     * @param title 标题关键词
     * @param pageable 分页信息
     * @return 话题分页列表
     */
    Page<Topic> findByTitleContaining(String title, Pageable pageable);
}
