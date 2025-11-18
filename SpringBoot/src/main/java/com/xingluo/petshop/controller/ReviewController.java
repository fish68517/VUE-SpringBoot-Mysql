package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreateReviewDTO;
import com.xingluo.petshop.dto.ReviewVO;
import com.xingluo.petshop.entity.Review;
import com.xingluo.petshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评价控制器
 * 提供商品评价的创建和查询等RESTful API
 */
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    
    private final ReviewService reviewService;
    
    /**
     * 创建评价
     * POST /api/review
     */
    @PostMapping
    public ApiResponse<ReviewVO> createReview(@RequestBody CreateReviewDTO createReviewDTO) {
        // 将DTO转换为实体
        Review review = new Review();
        review.setUserId(createReviewDTO.getUserId());
        review.setProductId(createReviewDTO.getProductId());
        review.setOrderId(createReviewDTO.getOrderId());
        review.setRating(createReviewDTO.getRating());
        review.setContent(createReviewDTO.getContent());
        review.setImages(createReviewDTO.getImages());
        
        // 调用服务层创建
        Review createdReview = reviewService.createReview(review);
        
        // 转换为VO返回
        ReviewVO reviewVO = convertToVO(createdReview);
        return ApiResponse.ok(reviewVO);
    }
    
    /**
     * 获取商品评价列表
     * GET /api/review/product/{id}
     */
    @GetMapping("/product/{id}")
    public ApiResponse<List<ReviewVO>> getProductReviews(@PathVariable Long id) {
        List<Review> reviews = reviewService.getProductReviews(id);
        
        // 转换为VO列表返回
        List<ReviewVO> reviewVOList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return ApiResponse.ok(reviewVOList);
    }
    
    /**
     * 获取用户评价列表
     * GET /api/review/user
     * 注意：实际项目中应该从token中获取userId，这里简化处理，通过请求参数传递
     */
    @GetMapping("/user")
    public ApiResponse<List<ReviewVO>> getUserReviews(@RequestParam Long userId) {
        List<Review> reviews = reviewService.getUserReviews(userId);
        
        // 转换为VO列表返回
        List<ReviewVO> reviewVOList = reviews.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return ApiResponse.ok(reviewVOList);
    }
    
    /**
     * 将Review实体转换为ReviewVO
     */
    private ReviewVO convertToVO(Review review) {
        ReviewVO reviewVO = new ReviewVO();
        BeanUtils.copyProperties(review, reviewVO);
        
        // 设置用户信息
        if (review.getUser() != null) {
            reviewVO.setUserNickname(review.getUser().getNickname());
            reviewVO.setUserAvatar(review.getUser().getAvatar());
        }
        
        // 设置商品信息
        if (review.getProduct() != null) {
            reviewVO.setProductName(review.getProduct().getName());
        }
        
        return reviewVO;
    }
}
