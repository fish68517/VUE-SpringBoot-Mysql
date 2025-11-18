package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 创建评价DTO
 */
@Data
public class CreateReviewDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 评分(1-5)
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String content;
    
    /**
     * 评价图片(JSON格式)
     */
    private String images;
}
