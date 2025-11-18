package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 创建宠物档案DTO
 */
@Data
public class CreatePetDTO {
    
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
}
