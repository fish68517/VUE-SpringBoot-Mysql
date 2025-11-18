package com.xingluo.petshop.dto;

import lombok.Data;

/**
 * 创建评论 DTO
 */
@Data
public class CreateReplyDTO {
    
    private Long postId;
    
    private String content;
}
