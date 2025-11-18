package com.xingluo.petshop.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 店铺VO
 */
@Data
public class ShopVO {
    
    /**
     * 店铺ID
     */
    private Long id;
    
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
    
    /**
     * 状态(0待审核/1正常/2禁用)
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
