package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作品更新请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkUpdateRequest {

    /**
     * 作品名称
     */
    private String title;

    /**
     * 作品描述
     */
    private String description;

    /**
     * 分类
     */
    private String category;

    /**
     * 作品图片URL
     */
    private String imageUrl;

    /**
     * 创作者名称
     */
    private String creator;

    /**
     * 刺绣技法
     */
    private String technique;

}
