package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资讯更新请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsUpdateRequest {

    /**
     * 资讯标题
     */
    private String title;

    /**
     * 资讯内容
     */
    private String content;

    /**
     * 资讯作者
     */
    private String author;
}
