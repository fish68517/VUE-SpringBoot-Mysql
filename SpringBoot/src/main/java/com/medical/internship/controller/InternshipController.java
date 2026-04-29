package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.*;
import com.medical.internship.service.InternshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 实习过程管理控制器
 */
@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipController {
    
    private final InternshipService internshipService;



    /**
     * 手动创建实习记录（学生发起）
     * POST /api/internships
     */
    @PostMapping
    public ApiResponse<String> createInternship(@RequestBody @Valid InternshipCreateRequest request) {
        System.out.println("收到手动创建实习请求: {}"+ request);
        internshipService.createInternship(request);
        return ApiResponse.success("实习记录添加成功", null);
    }
    
    /**
     * 获取实习记录列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InternshipResponse>>> getInternshipList() {
        List<InternshipResponse> internships = internshipService.getInternshipList();
        return ResponseEntity.ok(ApiResponse.success(internships));
    }
    
    /**
     * 获取实习记录详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipResponse>> getInternshipDetail(@PathVariable Long id) {
        InternshipResponse internship = internshipService.getInternshipDetail(id);
        return ResponseEntity.ok(ApiResponse.success(internship));
    }
    
    /**
     * 提交周记（学生）
     */
    @PostMapping("/{id}/weekly-reports")
    public ResponseEntity<ApiResponse<WeeklyReportResponse>> submitWeeklyReport(
            @PathVariable Long id,
            @Valid @RequestBody WeeklyReportCreateRequest request) {
        WeeklyReportResponse report = internshipService.submitWeeklyReport(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("周记提交成功", report));
    }
    
    /**
     * 获取周记列表
     */
    @GetMapping("/{id}/weekly-reports")
    public ResponseEntity<ApiResponse<List<WeeklyReportResponse>>> getWeeklyReportList(@PathVariable Long id) {
        List<WeeklyReportResponse> reports = internshipService.getWeeklyReportList(id);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    
    /**
     * 批阅周记（老师）
     */
    @PutMapping("/{id}/weekly-reports/{reportId}/review")
    public ResponseEntity<ApiResponse<WeeklyReportResponse>> reviewWeeklyReport(
            @PathVariable Long id,
            @PathVariable Long reportId,
            @Valid @RequestBody WeeklyReportReviewRequest request) {
        WeeklyReportResponse report = internshipService.reviewWeeklyReport(id, reportId, request);
        return ResponseEntity.ok(ApiResponse.success("周记批阅完成", report));
    }
    
    /**
     * 提交评价（支持老师评学生和学生评老师）
     */
    @PostMapping("/{id}/evaluations")
    public ResponseEntity<ApiResponse<EvaluationResponse>> submitEvaluation(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationCreateRequest request) {
        EvaluationResponse evaluation = internshipService.submitEvaluation(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("评价提交成功", evaluation));
    }
    
    /**
     * 获取评价列表
     */
    @GetMapping("/{id}/evaluations")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getEvaluationList(@PathVariable Long id) {
        List<EvaluationResponse> evaluations = internshipService.getEvaluationList(id);
        return ResponseEntity.ok(ApiResponse.success(evaluations));
    }
}
