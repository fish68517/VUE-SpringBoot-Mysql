package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息推送请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePushRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

}
