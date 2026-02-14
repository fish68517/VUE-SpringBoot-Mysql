package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 作品ID
     */
    private Long artworkId;

    /**
     * 话题ID
     */
    private Long topicId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 Comment 实体转换为 CommentResponse
     */
    public static CommentResponse fromComment(Comment comment, String username) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .username(username)
                .artworkId(comment.getArtworkId())
                .topicId(comment.getTopicId())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

}
