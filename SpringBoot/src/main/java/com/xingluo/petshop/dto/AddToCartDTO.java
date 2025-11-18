package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 添加到购物车DTO
 */
@Data
public class AddToCartDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 数量
     */
    private Integer quantity;
}
