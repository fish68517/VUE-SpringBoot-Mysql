package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识更新请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeUpdateRequest {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 分类
     */
    private String category;

    /**
     * 作者
     */
    private String author;

}
