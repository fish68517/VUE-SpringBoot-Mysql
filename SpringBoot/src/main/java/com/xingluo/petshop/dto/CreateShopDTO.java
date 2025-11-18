package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 创建店铺DTO
 */
@Data
public class CreateShopDTO {
    
    /**
     * 用户ID（店主）
     */
    private Long userId;
    
    /**
     * 店铺名称
     */
    private String name;
    
    /**
     * 店铺描述
     */
    private String description;
    
    /**
     * 店铺Logo
     */
    private String logo;
    
    /**
     * 联系方式
     */
    private String contact;
}
