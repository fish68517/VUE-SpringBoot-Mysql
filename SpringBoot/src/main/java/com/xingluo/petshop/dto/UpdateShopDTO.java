package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 更新店铺DTO
 */
@Data
public class UpdateShopDTO {
    
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
