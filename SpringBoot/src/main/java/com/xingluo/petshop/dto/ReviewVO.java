package com.xingluo.petshop.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价VO
 */
@Data
public class ReviewVO {
    
    /**
     * 评价ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户昵称
     */
    private String userNickname;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
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
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
