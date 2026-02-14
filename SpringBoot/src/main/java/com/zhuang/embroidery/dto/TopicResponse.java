package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 话题响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicResponse {

    /**
     * 话题ID
     */
    private Long id;

    /**
     * 话题标题
     */
    private String title;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 创建者用户名
     */
    private String creatorUsername;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 Topic 实体转换为 TopicResponse
     */
    public static TopicResponse fromTopic(Topic topic, String creatorUsername) {
        return TopicResponse.builder()
                .id(topic.getId())
                .title(topic.getTitle())
                .description(topic.getDescription())
                .isPinned(topic.getIsPinned())
                .createdBy(topic.getCreatedBy())
                .creatorUsername(creatorUsername)
                .createdAt(topic.getCreatedAt())
                .updatedAt(topic.getUpdatedAt())
                .build();
    }

}
