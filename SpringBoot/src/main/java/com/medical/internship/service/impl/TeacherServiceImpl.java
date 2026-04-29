package com.medical.internship.service.impl;

import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.*;
import com.medical.internship.entity.*;
import com.medical.internship.repository.*;
import com.medical.internship.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 带教老师服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeacherServiceImpl implements TeacherService {
    
    private final InternshipRepository internshipRepository;
    private final TaskRepository taskRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<StudentResponse> getStudentsByTeacher() {
        // 获取当前登录的老师ID
        Long teacherId = SessionContext.getCurrentUserId();
        
        // 查询该老师的所有实习记录
        List<Internship> internships = internshipRepository.findByTeacherId(teacherId);
        
        // 转换为StudentResponse
        return internships.stream()
                .map(this::convertToStudentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public StudentResponse getStudentDetail(Long internshipId) {
        // 获取实习记录
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("实习记录不存在"));
        
        // 验证权限 - 只有该实习的老师才能查看
        Long teacherId = SessionContext.getCurrentUserId();
        if (!internship.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限查看该学生信息");
        }
        
        return convertToStudentResponse(internship);
    }
    
    @Override
    public List<TaskResponse> getTasksByStudent(Long internshipId) {
        // 验证权限
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("实习记录不存在"));
        
        Long teacherId = SessionContext.getCurrentUserId();
        if (!internship.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限查看该学生的任务");
        }
        
        // 查询任务列表
        List<Task> tasks = taskRepository.findByInternshipId(internshipId);
        
        // 转换为TaskResponse
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public TaskResponse createTask(Long internshipId, TaskCreateRequest request) {
        // 获取实习记录
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("实习记录不存在"));
        
        // 验证权限 - 只有该实习的老师才能创建任务
        Long teacherId = SessionContext.getCurrentUserId();
        if (!internship.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限为该学生创建任务");
        }
        
        // 创建任务
        Task task = Task.builder()
                .internship(internship)
                .title(request.getTitle())
                .content(request.getContent())
                .deadline(request.getDeadline())
                .build();
        
        task = taskRepository.save(task);
        log.info("创建任务成功: {}", task.getId());
        
        return convertToTaskResponse(task);
    }
    
    @Override
    public List<WeeklyReportResponse> getWeeklyReportsByStudent(Long internshipId) {
        // 验证权限
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("实习记录不存在"));
        
        Long teacherId = SessionContext.getCurrentUserId();
        if (!internship.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限查看该学生的周记");
        }
        
        // 查询周记列表
        List<WeeklyReport> reports = weeklyReportRepository.findByInternshipId(internshipId);
        
        // 转换为WeeklyReportResponse
        return reports.stream()
                .map(this::convertToWeeklyReportResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<WeeklyReportResponse> getWeeklyReportsForTeacher() {
        // 获取当前登录的老师ID
        Long teacherId = SessionContext.getCurrentUserId();
        
        // 查询该老师的所有实习记录
        List<Internship> internships = internshipRepository.findByTeacherId(teacherId);
        
        // 查询这些实习记录的所有周记
        List<WeeklyReport> reports = internships.stream()
                .flatMap(internship -> weeklyReportRepository.findByInternshipId(internship.getId()).stream())
                .collect(Collectors.toList());
        
        // 转换为WeeklyReportResponse，并添加学生信息
        return reports.stream()
                .map(report -> {
                    WeeklyReportResponse response = convertToWeeklyReportResponse(report);
                    response.setStudentName(report.getInternship().getStudent().getUsername());
                    response.setInternshipId(report.getInternship().getId());
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<EvaluationResponse> getStudentEvaluationHistory(Long internshipId) {
        // 验证权限
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("实习记录不存在"));
        
        Long teacherId = SessionContext.getCurrentUserId();
        if (!internship.getTeacher().getId().equals(teacherId)) {
            throw new RuntimeException("没有权限查看该学生的评价");
        }
        
        // 查询评价列表
        List<Evaluation> evaluations = evaluationRepository.findByInternshipId(internshipId);
        
        // 转换为EvaluationResponse
        return evaluations.stream()
                .map(this::convertToEvaluationResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 将Internship转换为StudentResponse
     */
    private StudentResponse convertToStudentResponse(Internship internship) {
        return StudentResponse.builder()
                .internshipId(internship.getId())
                .studentId(internship.getStudent().getId())
                .studentName(internship.getStudent().getUsername())
                .postTitle(internship.getPost().getTitle())
                .department(internship.getPost().getDepartment())
                .startDate(internship.getStartDate())
                .endDate(internship.getEndDate())
                .internshipStatus(internship.getStatus())
                .build();
    }
    
    /**
     * 将Task转换为TaskResponse
     */
    private TaskResponse convertToTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .internshipId(task.getInternship().getId())
                .title(task.getTitle())
                .content(task.getContent())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .build();
    }
    
    /**
     * 将WeeklyReport转换为WeeklyReportResponse
     */
    private WeeklyReportResponse convertToWeeklyReportResponse(WeeklyReport report) {
        return WeeklyReportResponse.builder()
                .id(report.getId())
                .internshipId(report.getInternship().getId())
                .weekNumber(report.getWeekNumber())
                .content(report.getContent())
                .teacherComment(report.getTeacherComment())
                .teacherScore(report.getTeacherScore())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
    
    /**
     * 将Evaluation转换为EvaluationResponse
     */
    private EvaluationResponse convertToEvaluationResponse(Evaluation evaluation) {
        return EvaluationResponse.builder()
                .id(evaluation.getId())
                .internshipId(evaluation.getInternship().getId())
                .evaluatorId(evaluation.getEvaluator().getId())
                .evaluatorName(evaluation.getEvaluator().getUsername())
                .evaluatorType(evaluation.getEvaluatorType())
                .score(evaluation.getScore())
                .comment(evaluation.getComment())
                .createdAt(evaluation.getCreatedAt())
                .build();
    }
}
