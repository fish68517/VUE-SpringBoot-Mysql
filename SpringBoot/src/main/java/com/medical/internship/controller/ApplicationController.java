package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.ApplicationCreateRequest;
import com.medical.internship.dto.ApplicationResponse;
import com.medical.internship.dto.ApplicationReviewRequest;
import com.medical.internship.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 申请管理控制器
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    /**
     * 提交申请（学生）
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponse>> submitApplication(
            @Valid @RequestBody ApplicationCreateRequest request) {
        ApplicationResponse application = applicationService.submitApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("申请提交成功", application));
    }
    
    /**
     * 获取申请列表（根据用户身份返回相应数据）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicationList() {
        System.out.println("进入ApplicationController.getApplicationList方法");
        List<ApplicationResponse> applications = applicationService.getApplicationList();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
    
    /**
     * 获取申请详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationDetail(@PathVariable Long id) {
        ApplicationResponse application = applicationService.getApplicationDetail(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }
    
    /**
     * 学校审批申请
     */
    @PutMapping("/{id}/school-review")
    public ResponseEntity<ApiResponse<ApplicationResponse>> schoolReview(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationReviewRequest request) {
        ApplicationResponse application = applicationService.schoolReview(id, request);
        return ResponseEntity.ok(ApiResponse.success("学校审批完成", application));
    }
    
    /**
     * 医院审批申请
     */
    @PutMapping("/{id}/hospital-review")
    public ResponseEntity<ApiResponse<ApplicationResponse>> hospitalReview(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationReviewRequest request) {
        ApplicationResponse application = applicationService.hospitalReview(id, request);
        return ResponseEntity.ok(ApiResponse.success("医院审批完成", application));
    }


    @GetMapping("/hospital-review")
    public ResponseEntity<ApiResponse<java.util.List<ApplicationResponse>>> hospitalReview2222() {
        java.util.List<ApplicationResponse> application = applicationService.getApplicationList();
        return ResponseEntity.ok(ApiResponse.success(application));
    }
    
    /**
     * 获取学校审批的申请列表（学校管理员）
     */
    @GetMapping("/school-review")
    public ResponseEntity<ApiResponse<java.util.List<ApplicationResponse>>> getSchoolReviewApplications() {
        java.util.List<ApplicationResponse> applications = applicationService.getSchoolReviewApplications();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
}
