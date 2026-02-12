package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学校数据统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolStatisticsResponse {
    
    /**
     * 申请总人数
     */
    private Long totalApplications;
    
    /**
     * 通过人数（医院终审通过）
     */
    private Long approvedApplications;
    
    /**
     * 实习中人数
     */
    private Long ongoingInternships;
    
    /**
     * 已完成人数
     */
    private Long completedInternships;
    
    /**
     * 完成率（百分比）
     */
    private Double completionRate;
    
    /**
     * 驳回人数
     */
    private Long rejectedApplications;
}
