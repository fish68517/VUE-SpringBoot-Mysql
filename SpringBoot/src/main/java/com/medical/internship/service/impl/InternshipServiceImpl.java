package com.medical.internship.service.impl;

import com.medical.internship.common.AccessDeniedException;
import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.*;
import com.medical.internship.entity.*;
import com.medical.internship.repository.*;
import com.medical.internship.service.InternshipService;
import com.medical.internship.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实习过程管理服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional  // <--- 加上这一行
public class InternshipServiceImpl implements InternshipService {
    
    private final InternshipRepository internshipRepository;
    private final WeeklyReportRepository weeklyReportRepository;
    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 👇 如果你代码里还没有这三个，必须加上！
    private final ApplicationRepository applicationRepository;
    private final PostRepository postRepository;

    
    @Override
    public List<InternshipResponse> getInternshipList() {
        Long userId = SessionContext.getCurrentUserId();
        String userRole = SessionContext.getCurrentUserRole();
        
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        List<Internship> internships;
        
        if ("STUDENT".equals(userRole)) {
            // 学生只能看自己的实习记录
            internships = internshipRepository.findByStudentId(userId);
        } else if ("TEACHER".equals(userRole)) {
            // 老师只能看自己指导的实习记录
            internships = internshipRepository.findByTeacherId(userId);
        } else {
            throw new BusinessException("无权限查看实习记录列表");
        }
        
        return internships.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public InternshipResponse getInternshipDetail(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("实习记录不存在"));
        
        // 验证权限
        validateInternshipAccess(internship);
        
        return convertToResponse(internship);
    }
    
    @Override
    @Transactional
    public WeeklyReportResponse submitWeeklyReport(Long internshipId, WeeklyReportCreateRequest request) {
        // 验证用户角色
        String userRole = SessionContext.getCurrentUserRole();
        if (!"STUDENT".equals(userRole)) {
            throw new BusinessException("只有学生可以提交周记");
        }
        
        Long userId = SessionContext.getCurrentUserId();
        
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("实习记录不存在"));
        
        // 验证权限 - 学生只能提交自己的周记
        if (!internship.getStudent().getId().equals(userId)) {
            throw new BusinessException("无权限提交该实习记录的周记");
        }
        
        // 验证实习状态
        if (!"ONGOING".equals(internship.getStatus())) {
            throw new BusinessException("实习已结束，无法提交周记");
        }
        
        // 检查是否已提交过该周的周记
        java.util.Optional<WeeklyReport> existingReport = weeklyReportRepository
                .findByInternshipIdAndWeekNumber(internshipId, request.getWeekNumber());
        if (existingReport.isPresent()) {
            throw new BusinessException("该周周记已提交，无法重复提交");
        }
        
        // 创建周记
        WeeklyReport weeklyReport = WeeklyReport.builder()
                .internship(internship)
                .weekNumber(request.getWeekNumber())
                .content(request.getContent())
                .status("SUBMITTED")
                .build();
        
        WeeklyReport savedReport = weeklyReportRepository.save(weeklyReport);
        return convertWeeklyReportToResponse(savedReport);
    }
    
    @Override
    public List<WeeklyReportResponse> getWeeklyReportList(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("实习记录不存在"));
        
        // 验证权限
        validateInternshipAccess(internship);
        
        List<WeeklyReport> reports = weeklyReportRepository.findByInternshipId(internshipId);
        return reports.stream()
                .map(this::convertWeeklyReportToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public WeeklyReportResponse reviewWeeklyReport(Long internshipId, Long reportId, WeeklyReportReviewRequest request) {
        // 验证用户角色
        String userRole = SessionContext.getCurrentUserRole();
        if (!"TEACHER".equals(userRole)) {
            throw new BusinessException("只有老师可以批阅周记");
        }
        
        Long userId = SessionContext.getCurrentUserId();
        
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("实习记录不存在"));
        
        // 验证权限 - 老师只能批阅自己指导的学生的周记
        if (!internship.getTeacher().getId().equals(userId)) {
            throw new BusinessException("无权限批阅该周记");
        }
        
        WeeklyReport report = weeklyReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("周记不存在"));
        
        // 验证周记属于该实习记录
        if (!report.getInternship().getId().equals(internshipId)) {
            throw new BusinessException("周记不属于该实习记录");
        }
        
        // 验证周记状态
        if (!"SUBMITTED".equals(report.getStatus())) {
            throw new BusinessException("周记已批阅，无法重复批阅");
        }
        
        // 验证评分范围
        if (request.getTeacherScore() < 0 || request.getTeacherScore() > 100) {
            throw new BusinessException("评分范围应在0-100之间");
        }
        
        // 更新周记
        report.setStatus(request.getStatus());
        report.setTeacherComment(request.getTeacherComment());
        report.setTeacherScore(request.getTeacherScore());
        
        WeeklyReport updatedReport = weeklyReportRepository.save(report);
        
        // 如果周记被打回，创建通知
        if ("REJECTED".equals(request.getStatus())) {
            notificationService.createWeeklyReportRejectedNotification(
                    internship.getStudent(),
                    request.getTeacherComment()
            );
        }
        
        return convertWeeklyReportToResponse(updatedReport);
    }
    
    @Override
    @Transactional
    public EvaluationResponse submitEvaluation(Long internshipId, EvaluationCreateRequest request) {
        Long userId = SessionContext.getCurrentUserId();
        String userRole = SessionContext.getCurrentUserRole();
        
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("实习记录不存在"));
        
        // 验证权限
//        if ("TEACHER".equals(request.getEvaluatorType())) {
//            // 老师评学生
//            if (!internship.getTeacher().getId().equals(userId)) {
//                throw new BusinessException("无权限提交该评价");
//            }
//        } else if ("STUDENT".equals(request.getEvaluatorType())) {
//            // 学生评老师
//            if (!internship.getStudent().getId().equals(userId)) {
//                throw new BusinessException("无权限提交该评价");
//            }
//        } else {
//            throw new BusinessException("评价人类型不合法");
//        }
        
        // 验证评分范围
        if (request.getScore() < 0 || request.getScore() > 100) {
            throw new BusinessException("评分范围应在0-100之间");
        }
        
        User evaluator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        
        // 创建评价
        Evaluation evaluation = Evaluation.builder()
                .internship(internship)
                .evaluator(evaluator)
                .evaluatorType(request.getEvaluatorType())
                .score(request.getScore())
                .comment(request.getComment())
                .build();
        
        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return convertEvaluationToResponse(savedEvaluation);
    }
    
    @Override
    public List<EvaluationResponse> getEvaluationList(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("实习记录不存在"));
        
        // 验证权限
        validateInternshipAccess(internship);
        
        List<Evaluation> evaluations = evaluationRepository.findByInternshipId(internshipId);
        return evaluations.stream()
                .map(this::convertEvaluationToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 实现手动创建实习记录逻辑
     */
    @Override
    public void createInternship(InternshipCreateRequest request) {
        // 1. 从上下文中安全获取当前登录的学生ID
        Long studentId = SessionContext.getCurrentUserId();
        if (studentId == null) {
            throw new AccessDeniedException("用户未登录或会话已过期");
        }

        // 2. 校验防重防刷：同一条申请不能对应多条实习记录
        if (internshipRepository.existsByApplicationId(request.getApplicationId())) {
            throw new BusinessException("该申请已关联实习记录，请勿重复添加！");
        }

        // 3. 从数据库拉取全部需要的实体记录 (如果查不到直接抛异常)
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的申请记录"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的岗位记录"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到当前学生信息"));

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的带教老师信息"));

        // 4. 组装 Internship 实体
        Internship internship = new Internship();
        internship.setApplication(application);
        internship.setPost(post);
        internship.setStudent(student);
        internship.setTeacher(teacher);

        internship.setStartDate(request.getStartDate());
        internship.setEndDate(request.getEndDate());
        internship.setStatus(request.getStatus());

        // 5. 保存入库
        internshipRepository.save(internship);
    }

    /**
     * 验证用户是否有权限访问该实习记录
     */
    private void validateInternshipAccess(Internship internship) {
        Long userId = SessionContext.getCurrentUserId();
        String userRole = SessionContext.getCurrentUserRole();
        
        if ("STUDENT".equals(userRole)) {
            // 学生只能看自己的实习记录
            if (!internship.getStudent().getId().equals(userId)) {
                throw new BusinessException("无权限访问该实习记录");
            }
        } else if ("TEACHER".equals(userRole)) {
            // 老师只能看自己指导的实习记录
            if (!internship.getTeacher().getId().equals(userId)) {
                throw new BusinessException("无权限访问该实习记录");
            }
        } else {
            throw new BusinessException("无权限访问该实习记录");
        }
    }
    
    /**
     * 将Internship实体转换为InternshipResponse DTO
     */
    private InternshipResponse convertToResponse(Internship internship) {
        return InternshipResponse.builder()
                .id(internship.getId())
                .student(convertUserToResponse(internship.getStudent()))
                .teacher(convertUserToResponse(internship.getTeacher()))
                .post(convertPostToResponse(internship.getPost()))
                .startDate(internship.getStartDate())
                .endDate(internship.getEndDate())
                .status(internship.getStatus())
                .createdAt(internship.getCreatedAt())
                .updatedAt(internship.getUpdatedAt())
                .build();
    }
    
    /**
     * 将WeeklyReport实体转换为WeeklyReportResponse DTO
     */
    private WeeklyReportResponse convertWeeklyReportToResponse(WeeklyReport report) {
        return WeeklyReportResponse.builder()
                .id(report.getId())
                .weekNumber(report.getWeekNumber())
                .content(report.getContent())
                .teacherComment(report.getTeacherComment())
                .teacherScore(report.getTeacherScore())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
    
    /**
     * 将Evaluation实体转换为EvaluationResponse DTO
     */
    private EvaluationResponse convertEvaluationToResponse(Evaluation evaluation) {
        return EvaluationResponse.builder()
                .id(evaluation.getId())
                .evaluator(convertUserToResponse(evaluation.getEvaluator()))
                .evaluatorType(evaluation.getEvaluatorType())
                .score(evaluation.getScore())
                .comment(evaluation.getComment())
                .createdAt(evaluation.getCreatedAt())
                .build();
    }
    
    /**
     * 将User实体转换为UserResponse DTO
     */
    private UserResponse convertUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * 将Post实体转换为PostResponse DTO
     */
    private PostResponse convertPostToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .department(post.getDepartment())
                .description(post.getDescription())
                .quota(post.getQuota())
                .duration(post.getDuration())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
