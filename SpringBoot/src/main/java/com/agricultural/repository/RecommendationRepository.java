package com.agricultural.repository;

import com.agricultural.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 推荐数据访问层接口
 */
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    
    /**
     * 根据预警ID查询推荐列表
     */
    List<Recommendation> findByWarningId(Long warningId);
    
    /**
     * 根据用户ID查询推荐列表
     */
    List<Recommendation> findByUserId(Long userId);
    
    /**
     * 根据产品ID查询推荐列表
     */
    List<Recommendation> findByProductId(Long productId);
    
    /**
     * 根据推荐状态查询推荐列表
     */
    List<Recommendation> findByStatus(Recommendation.RecommendationStatus status);
    
    /**
     * 根据预警ID和用户ID查询推荐列表
     */
    List<Recommendation> findByWarningIdAndUserId(Long warningId, Long userId);
    
    /**
     * 根据预警ID查询并按优先级排序
     */
    @Query("SELECT r FROM Recommendation r WHERE r.warningId = :warningId ORDER BY r.priority ASC")
    List<Recommendation> findByWarningIdOrderByPriority(@Param("warningId") Long warningId);
    
    /**
     * 查询待处理的推荐列表
     */
    @Query("SELECT r FROM Recommendation r WHERE r.status = 'PENDING' ORDER BY r.priority ASC, r.createdAt DESC")
    List<Recommendation> findPendingRecommendations();
}
