package com.medical.internship.service;

import com.medical.internship.dto.*;

import java.util.List;

/**
 * 实习过程管理服务接口
 */
public interface InternshipService {
    
    /**
     * 获取实习记录列表
     */
    List<InternshipResponse> getInternshipList();
    
    /**
     * 获取实习记录详情
     */
    InternshipResponse getInternshipDetail(Long internshipId);
    
    /**
     * 提交周记（学生）
     */
    WeeklyReportResponse submitWeeklyReport(Long internshipId, WeeklyReportCreateRequest request);
    
    /**
     * 获取周记列表
     */
    List<WeeklyReportResponse> getWeeklyReportList(Long internshipId);
    
    /**
     * 批阅周记（老师）
     */
    WeeklyReportResponse reviewWeeklyReport(Long internshipId, Long reportId, WeeklyReportReviewRequest request);
    
    /**
     * 提交评价（支持老师评学生和学生评老师）
     */
    EvaluationResponse submitEvaluation(Long internshipId, EvaluationCreateRequest request);
    
    /**
     * 获取评价列表
     */
    List<EvaluationResponse> getEvaluationList(Long internshipId);
}
