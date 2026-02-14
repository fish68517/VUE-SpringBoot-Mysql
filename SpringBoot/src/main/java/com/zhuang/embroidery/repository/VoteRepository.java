package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 投票数据访问层接口
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    /**
     * 查询所有投票列表（分页）
     *
     * @param pageable 分页信息
     * @return 投票分页列表
     */
    Page<Vote> findAll(Pageable pageable);

    /**
     * 根据状态查询投票列表（分页）
     *
     * @param status 状态（active/closed）
     * @param pageable 分页信息
     * @return 投票分页列表
     */
    Page<Vote> findByStatus(String status, Pageable pageable);

    /**
     * 查询所有活跃投票
     *
     * @return 活跃投票列表
     */
    List<Vote> findByStatus(String status);

    /**
     * 根据标题模糊查询投票列表（分页）
     *
     * @param title 标题关键词
     * @param pageable 分页信息
     * @return 投票分页列表
     */
    Page<Vote> findByTitleContaining(String title, Pageable pageable);
}
