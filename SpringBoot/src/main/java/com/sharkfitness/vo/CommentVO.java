package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * VO for comment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {
    
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private String userAvatar;
    private Long dynamicId;
    private LocalDateTime createdAt;
}
