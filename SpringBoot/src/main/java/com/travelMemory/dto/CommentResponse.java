package com.travelMemory.dto;

import com.travelMemory.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;

    private Long travelRecordId;

    private Long userId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserInfo user;

    /**
     * Convert Comment entity to CommentResponse DTO
     * @param comment the comment entity
     * @return CommentResponse DTO
     */
    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .travelRecordId(comment.getTravelRecordId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    /**
     * Convert Comment entity to CommentResponse DTO with user info
     * @param comment the comment entity
     * @param user the user info
     * @return CommentResponse DTO
     */
    public static CommentResponse from(Comment comment, UserInfo user) {
        return CommentResponse.builder()
                .id(comment.getId())
                .travelRecordId(comment.getTravelRecordId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(user)
                .build();
    }
}
