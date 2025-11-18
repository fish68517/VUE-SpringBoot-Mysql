package com.xingluo.petshop.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 帖子视图对象
 */
@Data
public class PostVO {
    
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String userAvatar;
    
    private String title;
    
    private String content;
    
    private String images;
    
    private Integer likes;
    
    private Integer views;
    
    private Boolean isLiked;
    
    private LocalDateTime createTime;
}
