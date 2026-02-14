package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问统计响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessStatisticsResponse {

    /**
     * 总访问量
     */
    private Long totalVisits;

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 总作品数
     */
    private Long totalArtworks;

    /**
     * 总知识数
     */
    private Long totalKnowledge;

    /**
     * 总评论数
     */
    private Long totalComments;

    /**
     * 总话题数
     */
    private Long totalTopics;

    /**
     * 总投票数
     */
    private Long totalVotes;

    /**
     * 总反馈数
     */
    private Long totalFeedback;

}
