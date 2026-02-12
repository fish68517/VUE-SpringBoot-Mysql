package com.medical.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 医院数据统计响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalStatisticsResponse {
    
    /**
     * 岗位发布数
     */
    private Long totalPosts;
    
    /**
     * 岗位投递数（申请总数）
     */
    private Long totalApplications;
    
    /**
     * 岗位录用数（医院终审通过）
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
     * 驳回人数
     */
    private Long rejectedApplications;
}
