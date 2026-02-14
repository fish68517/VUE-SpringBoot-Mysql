package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 资讯响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsResponse {

    /**
     * 资讯ID
     */
    private Long id;

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

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从News实体转换为响应DTO
     */
    public static NewsResponse fromNews(News news) {
        return NewsResponse.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .author(news.getAuthor())
                .createdAt(news.getCreatedAt())
                .updatedAt(news.getUpdatedAt())
                .build();
    }
}
