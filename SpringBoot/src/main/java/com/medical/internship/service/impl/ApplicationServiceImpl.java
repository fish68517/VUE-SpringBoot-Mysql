package com.medical.internship.service.impl;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.*;
import com.medical.internship.entity.Application;
import com.medical.internship.entity.Internship;
import com.medical.internship.entity.Post;
import com.medical.internship.entity.User;
import com.medical.internship.repository.ApplicationRepository;
import com.medical.internship.repository.InternshipRepository;
import com.medical.internship.repository.PostRepository;
import com.medical.internship.repository.UserRepository;
import com.medical.internship.service.ApplicationService;
import com.medical.internship.service.NotificationService;
import com.medical.internship.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 申请服务实现类
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final PostService postService;
    private final NotificationService notificationService;
    
    @Override
    @Transactional
    public ApplicationResponse submitApplication(ApplicationCreateRequest request) {
        // 获取当前用户（学生）
        Long studentId = SessionContext.getCurrentUserId();
        if (studentId == null) {
            throw new BusinessException("用户未登录");
        }
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("学生不存在"));
        
        // 验证用户角色
        if (!"STUDENT".equals(student.getRole())) {
            throw new BusinessException("只有学生可以提交申请");
        }
        
        // 获取岗位
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("岗位不存在"));
        
        // 验证岗位状态
        if (!"PUBLISHED".equals(post.getStatus())) {
            throw new BusinessException("岗位已下架，无法申请");
        }
        
        // 检查岗位名额是否已满
        if (postService.isPostQuotaFull(post.getId())) {
            throw new BusinessException("岗位名额已满，无法申请");
        }
        
        // 检查是否已申请过该岗位
        List<Application> existingApplications = applicationRepository.findByStudentIdAndPostId(studentId, post.getId());
        if (!existingApplications.isEmpty()) {
            throw new BusinessException("您已申请过该岗位");
        }
        
        // 创建申请
        Application application = Application.builder()
                .student(student)
                .post(post)
                .reason(request.getReason())
                .schoolStatus("PENDING")
                .hospitalStatus("PENDING")
                .build();
        
        Application savedApplication = applicationRepository.save(application);
        return convertToResponse(savedApplication);
    }
    
    @Override
    public List<ApplicationResponse> getApplicationList() {
        Long userId = SessionContext.getCurrentUserId();
        String userRole = SessionContext.getCurrentUserRole();
        Long organizationId = SessionContext.getCurrentOrganizationId();
        
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        
        List<Application> applications;
        
        if ("STUDENT".equals(userRole)) {
            // 学生只能看自己的申请
            applications = applicationRepository.findByStudentId(userId);
        } else if ("SCHOOL_ADMIN".equals(userRole)) {
            // 学校管理员看本校学生的申请
            List<User> schoolStudents = userRepository.findByOrganizationIdAndRole(organizationId, "STUDENT");
            List<Long> studentIds = schoolStudents.stream().map(User::getId).collect(Collectors.toList());
            applications = applicationRepository.findAll().stream()
                    .filter(app -> studentIds.contains(app.getStudent().getId()))
                    .collect(Collectors.toList());
        } else if ("HOSPITAL_ADMIN".equals(userRole)) {
            // 医院管理员看本院岗位的申请
            List<Post> hospitalPosts = postRepository.findByHospitalId(organizationId);
            List<Long> postIds = hospitalPosts.stream().map(Post::getId).collect(Collectors.toList());
            applications = applicationRepository.findAll().stream()
                    .filter(app -> postIds.contains(app.getPost().getId()))
                    .collect(Collectors.toList());
        } else {
            throw new BusinessException("无权限查看申请列表");
        }
        
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public ApplicationResponse getApplicationDetail(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("申请不存在"));
        
        // 验证权限
        validateApplicationAccess(application);
        
        return convertToResponse(application);
    }
    
    @Override
    @Transactional
    public ApplicationResponse schoolReview(Long applicationId, ApplicationReviewRequest request) {
        // 验证用户角色
        String userRole = SessionContext.getCurrentUserRole();
        if (!"SCHOOL_ADMIN".equals(userRole)) {
            throw new BusinessException("只有学校管理员可以进行学校审批");
        }
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("申请不存在"));
        
        // 验证权限
        validateApplicationAccess(application);
        
        // 验证申请状态
        if (!"PENDING".equals(application.getSchoolStatus())) {
            throw new BusinessException("申请已审批，无法重复审批");
        }
        
        // 更新学校审批状态
        application.setSchoolStatus(request.getStatus());
        application.setSchoolOpinion(request.getOpinion());
        
        Application updatedApplication = applicationRepository.save(application);
        
        // 创建申请审批结果通知
        notificationService.createApplicationResultNotification(
                application.getStudent(),
                request.getStatus(),
                request.getOpinion()
        );
        
        return convertToResponse(updatedApplication);
    }
    
    @Override
    @Transactional
    public ApplicationResponse hospitalReview(Long applicationId, ApplicationReviewRequest request) {
        // 验证用户角色
        String userRole = SessionContext.getCurrentUserRole();
        if (!"HOSPITAL_ADMIN".equals(userRole)) {
            throw new BusinessException("只有医院管理员可以进行医院审批");
        }
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("申请不存在"));
        
        // 验证权限
        validateApplicationAccess(application);
        
        // 验证申请状态
        if (!"APPROVED".equals(application.getSchoolStatus())) {
            throw new BusinessException("学校未审批通过，无法进行医院审批");
        }
        
        if (!"PENDING".equals(application.getHospitalStatus())) {
            throw new BusinessException("申请已审批，无法重复审批");
        }
        
        // 更新医院审批状态
        application.setHospitalStatus(request.getStatus());
        application.setHospitalOpinion(request.getOpinion());
        
        // 如果医院审批通过，自动生成实习记录
        if ("APPROVED".equals(request.getStatus())) {
            createInternshipRecord(application);
        }
        
        Application updatedApplication = applicationRepository.save(application);
        
        // 创建申请审批结果通知
        notificationService.createApplicationResultNotification(
                application.getStudent(),
                request.getStatus(),
                request.getOpinion()
        );
        
        return convertToResponse(updatedApplication);
    }
    
    @Override
    public List<ApplicationResponse> getSchoolReviewApplications() {
        // 验证用户角色
        String userRole = SessionContext.getCurrentUserRole();
        if (!"SCHOOL_ADMIN".equals(userRole)) {
            throw new BusinessException("只有学校管理员可以查看学校审批申请");
        }
        
        Long organizationId = SessionContext.getCurrentOrganizationId();
        
        // 获取本校学生
        List<User> schoolStudents = userRepository.findByOrganizationIdAndRole(organizationId, "STUDENT");
        List<Long> studentIds = schoolStudents.stream().map(User::getId).collect(Collectors.toList());
        
        // 获取本校学生的申请
        List<Application> applications = applicationRepository.findAll().stream()
                .filter(app -> studentIds.contains(app.getStudent().getId()))
                .collect(Collectors.toList());
        
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建实习记录
     */
    @Transactional
    private void createInternshipRecord(Application application) {
        // 获取岗位信息
        Post post = application.getPost();
        
        // 获取带教老师（暂时使用医院管理员作为带教老师，实际应该由医院管理员分配）
        User teacher = userRepository.findByOrganizationIdAndRole(post.getHospital().getId(), "TEACHER")
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException("医院没有可用的带教老师"));
        
        // 计算实习开始和结束日期
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(post.getDuration());
        
        // 创建实习记录
        Internship internship = Internship.builder()
                .application(application)
                .student(application.getStudent())
                .teacher(teacher)
                .post(post)
                .startDate(startDate)
                .endDate(endDate)
                .status("ONGOING")
                .build();
        
        internshipRepository.save(internship);
    }
    
    /**
     * 验证用户是否有权限访问该申请
     */
    private void validateApplicationAccess(Application application) {
        Long userId = SessionContext.getCurrentUserId();
        String userRole = SessionContext.getCurrentUserRole();
        Long organizationId = SessionContext.getCurrentOrganizationId();
        
        if ("STUDENT".equals(userRole)) {
            // 学生只能看自己的申请
            if (!application.getStudent().getId().equals(userId)) {
                throw new BusinessException("无权限访问该申请");
            }
        } else if ("SCHOOL_ADMIN".equals(userRole)) {
            // 学校管理员只能看本校学生的申请
            if (!application.getStudent().getOrganization().getId().equals(organizationId)) {
                throw new BusinessException("无权限访问该申请");
            }
        } else if ("HOSPITAL_ADMIN".equals(userRole)) {
            // 医院管理员只能看本院岗位的申请
            if (!application.getPost().getHospital().getId().equals(organizationId)) {
                throw new BusinessException("无权限访问该申请");
            }
        }
    }
    
    /**
     * 将Application实体转换为ApplicationResponse DTO
     */
    private ApplicationResponse convertToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .student(convertUserToResponse(application.getStudent()))
                .post(convertPostToResponse(application.getPost()))
                .reason(application.getReason())
                .schoolStatus(application.getSchoolStatus())
                .hospitalStatus(application.getHospitalStatus())
                .schoolOpinion(application.getSchoolOpinion())
                .hospitalOpinion(application.getHospitalOpinion())
                .studentName(application.getStudent().getUsername())
                .postTitle(application.getPost().getTitle())
                .hospitalName(application.getPost().getHospital().getName())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
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
                .organization(convertOrganizationToResponse(user.getOrganization()))
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
    
    /**
     * 将Organization实体转换为OrganizationResponse DTO
     */
    private OrganizationResponse convertOrganizationToResponse(com.medical.internship.entity.Organization org) {
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .type(org.getType())
                .code(org.getCode())
                .createdAt(org.getCreatedAt())
                .build();
    }
}
