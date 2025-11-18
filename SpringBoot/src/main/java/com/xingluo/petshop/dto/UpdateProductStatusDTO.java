package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 更新商品状态DTO
 */
@Data
public class UpdateProductStatusDTO {
    
    /**
     * 店铺ID
     */
    private Long shopId;
    
    /**
     * 状态（0-下架/1-上架）
     */
    private Integer status;
}
