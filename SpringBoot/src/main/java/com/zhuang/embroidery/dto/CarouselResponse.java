package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 轮播内容响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarouselResponse {

    /**
     * 轮播 ID
     */
    private Long id;

    /**
     * 轮播标题
     */
    private String title;

    /**
     * 轮播描述
     */
    private String description;

    /**
     * 轮播图片 URL
     */
    private String imageUrl;

    /**
     * 轮播链接
     */
    private String link;

    /**
     * 排序顺序
     */
    private Integer order;

}
