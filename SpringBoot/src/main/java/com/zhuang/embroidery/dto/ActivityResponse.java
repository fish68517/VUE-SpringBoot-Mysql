package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponse {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动组织者
     */
    private String organizer;

    /**
     * 活动时间
     */
    private String activityTime;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从Activity实体转换为响应DTO
     */
    public static ActivityResponse fromActivity(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .organizer(activity.getOrganizer())
                .activityTime(activity.getActivityTime())
                .location(activity.getLocation())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }
}
