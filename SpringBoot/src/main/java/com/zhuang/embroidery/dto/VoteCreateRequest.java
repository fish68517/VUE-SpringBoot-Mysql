package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票创建请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteCreateRequest {

    /**
     * 投票标题
     */
    private String title;

    /**
     * 投票描述
     */
    private String description;

    /**
     * 投票选项列表
     */
    private List<String> options;

    /**
     * 结束时间
     */
    private LocalDateTime endAt;
}
