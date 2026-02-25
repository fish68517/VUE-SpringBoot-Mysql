package com.tourism.repository;

import com.tourism.entity.BrowsingHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 浏览历史数据访问接口
 */
@Repository
public interface BrowsingHistoryRepository extends JpaRepository<BrowsingHistory, Long> {
    
    /**
     * 根据用户ID查询浏览历史（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 浏览历史分页结果
     */
    Page<BrowsingHistory> findByUserId(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID查询所有浏览历史
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    List<BrowsingHistory> findByUserId(Long userId);
    
    /**
     * 根据用户ID和目标类型查询浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 浏览历史列表
     */
    List<BrowsingHistory> findByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 根据用户ID查询浏览历史（按创建时间倒序）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 浏览历史分页结果
     */
    Page<BrowsingHistory> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 统计用户的浏览历史数量
     * @param userId 用户ID
     * @return 浏览历史数量
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户指定类型的浏览历史数量
     * @param userId 用户ID
     * @param targetType 目标类型
     * @return 浏览历史数量
     */
    long countByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 根据用户ID和目标类型删除浏览历史
     * @param userId 用户ID
     * @param targetType 目标类型
     */
    void deleteByUserIdAndTargetType(Long userId, String targetType);
    
    /**
     * 根据用户ID删除所有浏览历史
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);
}
