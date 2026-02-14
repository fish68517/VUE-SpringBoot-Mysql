package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论创建请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    /**
     * 评论内容
     */
    private String content;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 作品ID（可选）
     */
    private Long artworkId;

    /**
     * 话题ID（可选）
     */
    private Long topicId;

    /**
     * 父评论ID（用于回复，可选）
     */
    private Long parentId;

}
