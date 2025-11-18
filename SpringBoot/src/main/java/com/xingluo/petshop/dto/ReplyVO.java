package com.xingluo.petshop.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评论视图对象
 */
@Data
public class ReplyVO {
    
    private Long id;
    
    private Long postId;
    
    private Long userId;
    
    private String username;
    
    private String userAvatar;
    
    private String content;
    
    private LocalDateTime createTime;
}
