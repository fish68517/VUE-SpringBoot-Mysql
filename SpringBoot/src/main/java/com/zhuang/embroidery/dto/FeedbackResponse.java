package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 反馈响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {

    /**
     * 反馈ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 状态：pending/processed
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 Feedback 实体转换为 FeedbackResponse
     */
    public static FeedbackResponse fromFeedback(Feedback feedback, String username) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .userId(feedback.getUserId())
                .username(username)
                .content(feedback.getContent())
                .email(feedback.getEmail())
                .status(feedback.getStatus())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }

}
