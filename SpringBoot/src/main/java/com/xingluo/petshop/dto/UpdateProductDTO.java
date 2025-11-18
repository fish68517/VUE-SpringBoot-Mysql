package com.xingluo.petshop.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 更新商品DTO
 */
@Data
public class UpdateProductDTO {
    
    /**
     * 店铺ID
     */
    private Long shopId;
    
    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 库存
     */
    private Integer stock;
    
    /**
     * 主图URL
     */
    private String image;
    
    /**
     * 商品图片(JSON)
     */
    private String images;
}
