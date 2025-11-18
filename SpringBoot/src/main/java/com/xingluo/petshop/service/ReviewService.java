package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Review;

import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {
    
    /**
     * 创建评价
     * @param review 评价信息
     * @return 创建的评价
     */
    Review createReview(Review review);
    
    /**
     * 查询商品评价列表
     * @param productId 商品ID
     * @return 评价列表
     */
    List<Review> getProductReviews(Long productId);
    
    /**
     * 查询用户评价列表
     * @param userId 用户ID
     * @return 评价列表
     */
    List<Review> getUserReviews(Long userId);
}
