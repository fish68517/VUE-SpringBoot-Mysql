package com.xingluo.petshop.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 宠物档案VO
 */
@Data
public class PetVO {
    
    /**
     * 宠物ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 宠物名称
     */
    private String name;
    
    /**
     * 种类
     */
    private String species;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 性别
     */
    private String gender;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
