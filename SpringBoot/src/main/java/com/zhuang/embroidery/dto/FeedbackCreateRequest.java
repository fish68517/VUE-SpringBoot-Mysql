package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 反馈创建请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCreateRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系邮箱
     */
    private String email;

}
