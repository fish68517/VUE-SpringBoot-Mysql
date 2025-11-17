package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VO for dynamic post response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicVO {
    
    private Long id;
    private String content;
    private String imageUrls;
    private Long userId;
    private String username;
    private String userAvatar;
    private Integer likeCount;
    private Integer commentCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
