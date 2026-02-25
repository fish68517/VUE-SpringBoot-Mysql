package com.tourism.repository;

import com.tourism.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 留言数据访问接口
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * 根据目标类型和目标ID查询留言列表（分页）
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<Comment> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);
    
    /**
     * 根据目标类型和目标ID查询已批准的留言列表（分页）
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param status 留言状态
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<Comment> findByTargetTypeAndTargetIdAndStatus(String targetType, Long targetId, String status, Pageable pageable);
    
    /**
     * 根据用户ID查询留言列表（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<Comment> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据状态查询留言列表（分页）
     * @param status 留言状态
     * @param pageable 分页参数
     * @return 留言分页结果
     */
    Page<Comment> findByStatus(String status, Pageable pageable);
    
    /**
     * 根据目标类型和目标ID查询所有留言
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 留言列表
     */
    List<Comment> findByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * 根据目标类型和目标ID查询已批准的留言列表
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param status 留言状态
     * @return 留言列表
     */
    List<Comment> findByTargetTypeAndTargetIdAndStatus(String targetType, Long targetId, String status);
    
    /**
     * 根据用户ID查询所有留言
     * @param userId 用户ID
     * @return 留言列表
     */
    List<Comment> findByUserId(Long userId);
    
    /**
     * 统计指定目标的留言数量
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 留言数量
     */
    long countByTargetTypeAndTargetId(String targetType, Long targetId);
    
    /**
     * 统计指定目标已批准的留言数量
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param status 留言状态
     * @return 留言数量
     */
    long countByTargetTypeAndTargetIdAndStatus(String targetType, Long targetId, String status);
    
    /**
     * 统计指定状态的留言数量
     * @param status 留言状态
     * @return 留言数量
     */
    long countByStatus(String status);
    
    /**
     * 查询指定目标的置顶留言
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param isPinned 是否置顶
     * @return 置顶留言列表
     */
    List<Comment> findByTargetTypeAndTargetIdAndIsPinned(String targetType, Long targetId, Boolean isPinned);
    
    /**
     * 查询指定目标已批准且置顶的留言
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param status 留言状态
     * @param isPinned 是否置顶
     * @return 置顶留言列表
     */
    List<Comment> findByTargetTypeAndTargetIdAndStatusAndIsPinned(String targetType, Long targetId, String status, Boolean isPinned);
}
