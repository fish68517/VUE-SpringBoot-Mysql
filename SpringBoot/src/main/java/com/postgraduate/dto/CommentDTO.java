package com.postgraduate.dto;

import com.postgraduate.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Comment information in API responses.
 * Contains comment content, author information, and timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;
    private Long schoolId;
    private Long userId;
    private Long parentId;
    private String content;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorUsername;

    public static CommentDTO fromEntity(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDTO.builder()
                .id(comment.getId())
                .schoolId(comment.getSchoolId())
                .userId(comment.getUserId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .authorUsername(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .build();
    }

    public static CommentDTO fromEntityWithAuthor(Comment comment, String authorUsername) {
        if (comment == null) {
            return null;
        }
        return CommentDTO.builder()
                .id(comment.getId())
                .schoolId(comment.getSchoolId())
                .userId(comment.getUserId())
                .parentId(comment.getParentId())
                .content(comment.getContent())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .authorUsername(authorUsername)
                .build();
    }
}
