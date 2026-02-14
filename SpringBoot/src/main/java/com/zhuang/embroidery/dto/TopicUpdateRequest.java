package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 话题更新请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicUpdateRequest {

    /**
     * 话题标题
     */
    private String title;

    /**
     * 话题描述
     */
    private String description;

}
