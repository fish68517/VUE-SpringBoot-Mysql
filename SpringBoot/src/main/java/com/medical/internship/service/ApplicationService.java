package com.medical.internship.service;

import com.medical.internship.dto.ApplicationCreateRequest;
import com.medical.internship.dto.ApplicationResponse;
import com.medical.internship.dto.ApplicationReviewRequest;

import java.util.List;

/**
 * 申请服务接口
 */
public interface ApplicationService {
    
    /**
     * 提交申请（学生）
     */
    ApplicationResponse submitApplication(ApplicationCreateRequest request);
    
    /**
     * 获取申请列表（根据用户身份返回相应数据）
     */
    List<ApplicationResponse> getApplicationList();
    
    /**
     * 获取申请详情
     */
    ApplicationResponse getApplicationDetail(Long applicationId);
    
    /**
     * 学校审批申请
     */
    ApplicationResponse schoolReview(Long applicationId, ApplicationReviewRequest request);
    
    /**
     * 医院审批申请
     */
    ApplicationResponse hospitalReview(Long applicationId, ApplicationReviewRequest request);
    
    /**
     * 获取学校审批的申请列表（学校管理员）
     */
    java.util.List<ApplicationResponse> getSchoolReviewApplications();
}
