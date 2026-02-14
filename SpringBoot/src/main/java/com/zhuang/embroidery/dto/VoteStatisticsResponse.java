package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 投票统计响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteStatisticsResponse {

    /**
     * 投票ID
     */
    private Long id;

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
     * 状态：active/closed
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 结束时间
     */
    private LocalDateTime endAt;

    /**
     * 投票统计数据（选项 -> 投票数）
     */
    private Map<String, Long> statistics;

    /**
     * 总投票数
     */
    private Long totalVotes;
}
