package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.common.exception.BusinessException;
import com.xingluo.petshop.entity.Order;
import com.xingluo.petshop.entity.Review;
import com.xingluo.petshop.repository.OrderRepository;
import com.xingluo.petshop.repository.ReviewRepository;
import com.xingluo.petshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评价服务实现类
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    
    @Override
    @Transactional
    public Review createReview(Review review) {
        // 验证订单是否存在
        Order order = orderRepository.findById(review.getOrderId())
                .orElseThrow(() -> new BusinessException("订单不存在"));
        
        // 验证订单状态（只有已完成的订单才能评价，状态为3）
        if (order.getStatus() != 3) {
            throw new BusinessException("只有已完成的订单才能评价");
        }
        
        // 验证订单是否属于该用户
        if (!order.getUserId().equals(review.getUserId())) {
            throw new BusinessException("无权评价该订单");
        }
        
        // 验证是否已经评价过
        if (reviewRepository.existsByOrderIdAndProductId(review.getOrderId(), review.getProductId())) {
            throw new BusinessException("该商品已评价，不能重复评价");
        }
        
        // 验证评分范围
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        
        return reviewRepository.save(review);
    }
    
    @Override
    public List<Review> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreateTimeDesc(productId);
    }
    
    @Override
    public List<Review> getUserReviews(Long userId) {
        return reviewRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
}
