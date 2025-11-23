package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 创建帖子 DTO
 */
@Data
public class CreatePostDTO {
    
    private String title;
    
    private String content;
    
    private String images;
    private Long userId;
}
