package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评价 Repository
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * 根据商品ID查询评价列表
     */
    List<Review> findByProductIdOrderByCreateTimeDesc(Long productId);
    
    /**
     * 根据用户ID查询评价列表
     */
    List<Review> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 检查订单是否已评价
     */
    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
}
