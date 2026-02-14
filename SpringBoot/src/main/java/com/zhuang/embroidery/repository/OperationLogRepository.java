package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据访问层接口
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    /**
     * 根据用户ID查询操作日志列表
     *
     * @param userId 用户ID
     * @param pageable 分页信息
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据操作类型查询操作日志列表
     *
     * @param action 操作类型
     * @param pageable 分页信息
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByAction(String action, Pageable pageable);

    /**
     * 根据用户ID和操作类型查询操作日志列表
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @param pageable 分页信息
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByUserIdAndAction(Long userId, String action, Pageable pageable);

    /**
     * 根据目标类型查询操作日志列表
     *
     * @param targetType 目标类型
     * @param pageable 分页信息
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByTargetType(String targetType, Pageable pageable);

    /**
     * 根据时间范围查询操作日志列表
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页信息
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据用户ID查询最近的操作日志列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 操作日志列表
     */
    List<OperationLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
