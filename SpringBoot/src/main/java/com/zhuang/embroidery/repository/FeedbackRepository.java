package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 反馈数据访问层接口
 */
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * 根据用户ID查询反馈列表
     *
     * @param userId 用户ID
     * @return 反馈列表
     */
    List<Feedback> findByUserId(Long userId);

    /**
     * 根据状态查询反馈列表（分页）
     *
     * @param status 状态
     * @param pageable 分页信息
     * @return 反馈分页列表
     */
    Page<Feedback> findByStatus(String status, Pageable pageable);

    /**
     * 查询所有反馈（分页）
     *
     * @param pageable 分页信息
     * @return 反馈分页列表
     */
    Page<Feedback> findAll(Pageable pageable);
}
