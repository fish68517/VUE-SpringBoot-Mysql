package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核心功能导航响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureResponse {

    /**
     * 功能 ID
     */
    private Long id;

    /**
     * 功能名称
     */
    private String name;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 功能图标 URL
     */
    private String iconUrl;

    /**
     * 功能链接
     */
    private String link;

    /**
     * 排序顺序
     */
    private Integer order;

}
