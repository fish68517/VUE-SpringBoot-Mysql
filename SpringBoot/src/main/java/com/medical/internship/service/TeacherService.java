package com.medical.internship.service;

import com.medical.internship.dto.*;

import java.util.List;

/**
 * 带教老师服务接口
 */
public interface TeacherService {
    
    /**
     * 获取分配给老师的学生列表
     */
    List<StudentResponse> getStudentsByTeacher();
    
    /**
     * 获取学生详情
     */
    StudentResponse getStudentDetail(Long internshipId);
    
    /**
     * 获取学生的任务列表
     */
    List<TaskResponse> getTasksByStudent(Long internshipId);
    
    /**
     * 为学生创建任务
     */
    TaskResponse createTask(Long internshipId, TaskCreateRequest request);
    
    /**
     * 获取学生的周记列表
     */
    List<WeeklyReportResponse> getWeeklyReportsByStudent(Long internshipId);
    
    /**
     * 获取待批阅的周记列表
     */
    List<WeeklyReportResponse> getWeeklyReportsForTeacher();
    
    /**
     * 获取学生的评价历史
     */
    List<EvaluationResponse> getStudentEvaluationHistory(Long internshipId);
}
