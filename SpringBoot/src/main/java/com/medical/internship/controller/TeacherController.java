package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.*;
import com.medical.internship.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 带教老师控制器
 */
@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    
    /**
     * 获取分配给老师的学生列表
     */
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByTeacher() {
        List<StudentResponse> students = teacherService.getStudentsByTeacher();
        return ResponseEntity.ok(ApiResponse.success(students));
    }
    
    /**
     * 获取学生详情
     */
    @GetMapping("/students/{internshipId}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentDetail(@PathVariable Long internshipId) {
        StudentResponse student = teacherService.getStudentDetail(internshipId);
        return ResponseEntity.ok(ApiResponse.success(student));
    }
    
    /**
     * 获取学生的任务列表
     */
    @GetMapping("/students/{internshipId}/tasks")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByStudent(@PathVariable Long internshipId) {
        List<TaskResponse> tasks = teacherService.getTasksByStudent(internshipId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    /**
     * 为学生创建任务
     */
    @PostMapping("/students/{internshipId}/tasks")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable Long internshipId,
            @Valid @RequestBody TaskCreateRequest request) {
        TaskResponse task = teacherService.createTask(internshipId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("任务创建成功", task));
    }
    
    /**
     * 获取学生的周记列表
     */
    @GetMapping("/students/{internshipId}/weekly-reports")
    public ResponseEntity<ApiResponse<List<WeeklyReportResponse>>> getWeeklyReportsByStudent(
            @PathVariable Long internshipId) {
        List<WeeklyReportResponse> reports = teacherService.getWeeklyReportsByStudent(internshipId);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    
    /**
     * 获取待批阅的周记列表
     */
    @GetMapping("/weekly-reports")
    public ResponseEntity<ApiResponse<List<WeeklyReportResponse>>> getWeeklyReportsForTeacher() {
        List<WeeklyReportResponse> reports = teacherService.getWeeklyReportsForTeacher();
        return ResponseEntity.ok(ApiResponse.success(reports));
    }
    
    /**
     * 获取学生的评价历史
     */
    @GetMapping("/students/{internshipId}/evaluations")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> getStudentEvaluationHistory(
            @PathVariable Long internshipId) {
        List<EvaluationResponse> evaluations = teacherService.getStudentEvaluationHistory(internshipId);
        return ResponseEntity.ok(ApiResponse.success(evaluations));
    }
}
