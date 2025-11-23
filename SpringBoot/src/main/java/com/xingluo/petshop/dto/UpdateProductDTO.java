package com.xingluo.petshop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List; // 引入 List

/**
 * 更新商品DTO
 */
@Data
public class UpdateProductDTO {

    private Long shopId;

    private Long categoryId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String image;

    /**
     * 商品图片列表 (接收 JSON 数组)
     */
    private List<String> images; // 修改为 List<String>
}