package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Knowledge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeResponse {

    /**
     * 知识ID
     */
    private Long id;

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

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 Knowledge 实体转换为 KnowledgeResponse
     */
    public static KnowledgeResponse fromKnowledge(Knowledge knowledge) {
        return KnowledgeResponse.builder()
                .id(knowledge.getId())
                .title(knowledge.getTitle())
                .content(knowledge.getContent())
                .category(knowledge.getCategory())
                .author(knowledge.getAuthor())
                .viewCount(knowledge.getViewCount())
                .createdAt(knowledge.getCreatedAt())
                .updatedAt(knowledge.getUpdatedAt())
                .build();
    }

}
