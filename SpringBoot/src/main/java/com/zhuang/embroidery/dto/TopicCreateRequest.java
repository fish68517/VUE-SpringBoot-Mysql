package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 话题创建请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCreateRequest {

    /**
     * 话题标题
     */
    private String title;

    /**
     * 话题描述
     */
    private String description;

    /**
     * 创建者ID
     */
    private Long createdBy;

}
