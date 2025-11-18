package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 更新库存 DTO
 */
@Data
public class UpdateStockDTO {
    
    /**
     * 店铺ID
     */
    private Long shopId;
    
    /**
     * 新库存数量
     */
    private Integer stock;
}
