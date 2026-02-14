package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动创建请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityCreateRequest {

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
}
