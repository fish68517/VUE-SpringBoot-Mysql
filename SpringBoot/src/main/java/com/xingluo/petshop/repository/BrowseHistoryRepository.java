package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.BrowseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 浏览历史Repository接口
 */
@Repository
public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {
    
    /**
     * 获取用户浏览历史（按时间倒序）
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    List<BrowseHistory> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 获取用户最近浏览的N条记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 浏览历史列表
     */
    @Query("SELECT bh FROM BrowseHistory bh WHERE bh.userId = :userId ORDER BY bh.createTime DESC")
    List<BrowseHistory> findRecentByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否已浏览过该商品
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 浏览历史记录
     */
    BrowseHistory findFirstByUserIdAndProductIdOrderByCreateTimeDesc(Long userId, Long productId);
}
