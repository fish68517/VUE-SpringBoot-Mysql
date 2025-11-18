package com.xingluo.petshop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品VO
 */
@Data
public class ProductVO {
    
    private Long id;
    private Long shopId;
    private String shopName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String images;
    private Integer status;
    private Integer sales;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
