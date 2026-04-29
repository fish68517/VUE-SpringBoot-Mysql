package com.medical.internship.integration;

import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.*;
import com.medical.internship.entity.*;
import com.medical.internship.repository.*;
import com.medical.internship.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 系统集成测试 - 测试核心业务流程
 */
@SpringBootTest
@Transactional
public class SystemIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private InternshipService internshipService;
    
    @Autowired
    private StatisticsService statisticsService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private InternshipRepository internshipRepository;
    
    private Organization school1;
    private Organization school2;
    private Organization hospital1;
    private Organization hospital2;
    private UserResponse schoolAdmin1;
    private UserResponse schoolAdmin2;
    private UserResponse hospitalAdmin1;
    private UserResponse hospitalAdmin2;
    private UserResponse student1;
    private UserResponse student2;
    private UserResponse teacher1;
    
    @BeforeEach
    public void setUp() {
        // 创建两个学校
        school1 = organizationRepository.save(Organization.builder()
                .name("学校1")
                .type("SCHOOL")
                .code("SCHOOL_001")
                .createdAt(LocalDateTime.now())
                .build());
        
        school2 = organizationRepository.save(Organization.builder()
                .name("学校2")
                .type("SCHOOL")
                .code("SCHOOL_002")
                .createdAt(LocalDateTime.now())
                .build());
        
        // 创建两个医院
        hospital1 = organizationRepository.save(Organization.builder()
                .name("医院1")
                .type("HOSPITAL")
                .code("HOSPITAL_001")
                .createdAt(LocalDateTime.now())
                .build());
        
        hospital2 = organizationRepository.save(Organization.builder()
                .name("医院2")
                .type("HOSPITAL")
                .code("HOSPITAL_002")
                .createdAt(LocalDateTime.now())
                .build());
        
        // 创建学校管理员
        User schoolAdminUser1 = userRepository.save(User.builder()
                .username("school_admin_1")
                .password("password")
                .email("admin1@school1.com")
                .role("SCHOOL_ADMIN")
                .organization(school1)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        User schoolAdminUser2 = userRepository.save(User.builder()
                .username("school_admin_2")
                .password("password")
                .email("admin1@school2.com")
                .role("SCHOOL_ADMIN")
                .organization(school2)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        // 创建医院管理员
        User hospitalAdminUser1 = userRepository.save(User.builder()
                .username("hospital_admin_1")
                .password("password")
                .email("admin1@hospital1.com")
                .role("HOSPITAL_ADMIN")
                .organization(hospital1)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        User hospitalAdminUser2 = userRepository.save(User.builder()
                .username("hospital_admin_2")
                .password("password")
                .email("admin1@hospital2.com")
                .role("HOSPITAL_ADMIN")
                .organization(hospital2)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        // 创建学生
        User studentUser1 = userRepository.save(User.builder()
                .username("student_1")
                .password("password")
                .email("student1@school1.com")
                .role("STUDENT")
                .organization(school1)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        User studentUser2 = userRepository.save(User.builder()
                .username("student_2")
                .password("password")
                .email("student2@school2.com")
                .role("STUDENT")
                .organization(school2)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        // 创建带教老师
        User teacherUser1 = userRepository.save(User.builder()
                .username("teacher_1")
                .password("password")
                .email("teacher1@hospital1.com")
                .role("TEACHER")
                .organization(hospital1)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        // 转换为Response对象
        schoolAdmin1 = convertUserToResponse(schoolAdminUser1);
        schoolAdmin2 = convertUserToResponse(schoolAdminUser2);
        hospitalAdmin1 = convertUserToResponse(hospitalAdminUser1);
        hospitalAdmin2 = convertUserToResponse(hospitalAdminUser2);
        student1 = convertUserToResponse(studentUser1);
        student2 = convertUserToResponse(studentUser2);
        teacher1 = convertUserToResponse(teacherUser1);
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    /**
     * 测试1: 用户认证与组织隔离
     * 验证不同角色的用户只能看到自己组织的数据
     */
    @Test
    public void testUserAuthenticationAndOrganizationIsolation() {
        // 学校1的管理员登录
        SessionContext.setCurrentUser(schoolAdmin1);
        
        // 学校1的管理员应该能看到学校1的学生
        List<UserResponse> school1Students = userService.getPendingStudents(school1.getId());
        assertNotNull(school1Students);
        
        // 切换到学校2的管理员
        SessionContext.setCurrentUser(schoolAdmin2);
        
        // 学校2的管理员应该能看到学校2的学生
        List<UserResponse> school2Students = userService.getPendingStudents(school2.getId());
        assertNotNull(school2Students);
        
        // 验证数据隔离 - 学校1的管理员不应该能访问学校2的数据
        SessionContext.setCurrentUser(schoolAdmin1);
        assertThrows(Exception.class, () -> userService.getPendingStudents(school2.getId()));
    }
    
    /**
     * 测试2: 岗位管理与学校可见性控制
     * 验证医院发布的岗位只对指定学校可见
     */
    @Test
    public void testPostManagementAndSchoolVisibility() {
        // 医院1的管理员创建岗位，仅对学校1可见
        SessionContext.setCurrentUser(hospitalAdmin1);
        
        PostCreateRequest postRequest = PostCreateRequest.builder()
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(school1.getId()))
                .build();
        
        PostResponse createdPost = postService.createPost(postRequest);
        assertNotNull(createdPost);
        assertEquals("DRAFT", createdPost.getStatus());
        
        // 发布岗位
        PostStatusUpdateRequest statusRequest = PostStatusUpdateRequest.builder()
                .status("PUBLISHED")
                .build();
        
        PostResponse publishedPost = postService.updatePostStatus(createdPost.getId(), statusRequest);
        assertEquals("PUBLISHED", publishedPost.getStatus());
        
        // 学校1的学生应该能看到这个岗位
        SessionContext.setCurrentUser(student1);
        List<PostResponse> student1Posts = postService.getPostList();
        assertTrue(student1Posts.stream().anyMatch(p -> p.getId().equals(createdPost.getId())));
        
        // 学校2的学生不应该能看到这个岗位
        SessionContext.setCurrentUser(student2);
        List<PostResponse> student2Posts = postService.getPostList();
        assertFalse(student2Posts.stream().anyMatch(p -> p.getId().equals(createdPost.getId())));
    }
    
    /**
     * 测试3: 申请审批流程（学校初审 → 医院终审）
     * 验证完整的申请审批流程
     */
    @Test
    public void testApplicationApprovalFlow() {
        // 准备：医院1创建岗位
        SessionContext.setCurrentUser(hospitalAdmin1);
        PostCreateRequest postRequest = PostCreateRequest.builder()
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(school1.getId()))
                .build();
        
        PostResponse post = postService.createPost(postRequest);
        postService.updatePostStatus(post.getId(), PostStatusUpdateRequest.builder().status("PUBLISHED").build());
        
        // 学生1提交申请
        SessionContext.setCurrentUser(student1);
        ApplicationCreateRequest appRequest = ApplicationCreateRequest.builder()
                .postId(post.getId())
                .reason("我想在内科实习")
                .build();
        
        ApplicationResponse application = applicationService.submitApplication(appRequest);
        assertNotNull(application);
        assertEquals("PENDING", application.getSchoolStatus());
        assertEquals("PENDING", application.getHospitalStatus());
        
        // 学校1的管理员审批通过
        SessionContext.setCurrentUser(schoolAdmin1);
        ApplicationReviewRequest schoolReview = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        ApplicationResponse schoolApprovedApp = applicationService.schoolReview(application.getId(), schoolReview);
        assertEquals("APPROVED", schoolApprovedApp.getSchoolStatus());
        assertEquals("PENDING", schoolApprovedApp.getHospitalStatus());
        
        // 医院1的管理员终审通过
        SessionContext.setCurrentUser(hospitalAdmin1);
        ApplicationReviewRequest hospitalReview = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        ApplicationResponse hospitalApprovedApp = applicationService.hospitalReview(application.getId(), hospitalReview);
        assertEquals("APPROVED", hospitalApprovedApp.getSchoolStatus());
        assertEquals("APPROVED", hospitalApprovedApp.getHospitalStatus());
        
        // 验证实习记录已生成
        SessionContext.setCurrentUser(student1);
        List<InternshipResponse> internships = internshipService.getInternshipList();
        assertTrue(internships.size() > 0);
    }
    
    /**
     * 测试4: 实习过程管理（任务、周记、评价）
     * 验证实习过程中的周记提交和批阅
     */
    @Test
    public void testInternshipProcessManagement() {
        // 准备：创建实习记录
        SessionContext.setCurrentUser(hospitalAdmin1);
        PostCreateRequest postRequest = PostCreateRequest.builder()
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(school1.getId()))
                .build();
        
        PostResponse post = postService.createPost(postRequest);
        postService.updatePostStatus(post.getId(), PostStatusUpdateRequest.builder().status("PUBLISHED").build());
        
        // 学生申请并通过审批
        SessionContext.setCurrentUser(student1);
        ApplicationCreateRequest appRequest = ApplicationCreateRequest.builder()
                .postId(post.getId())
                .reason("我想在内科实习")
                .build();
        
        ApplicationResponse application = applicationService.submitApplication(appRequest);
        
        SessionContext.setCurrentUser(schoolAdmin1);
        applicationService.schoolReview(application.getId(), ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意")
                .build());
        
        SessionContext.setCurrentUser(hospitalAdmin1);
        applicationService.hospitalReview(application.getId(), ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意")
                .build());
        
        // 获取实习记录
        SessionContext.setCurrentUser(student1);
        List<InternshipResponse> internships = internshipService.getInternshipList();
        assertTrue(internships.size() > 0);
        InternshipResponse internship = internships.get(0);
        
        // 学生提交周记
        WeeklyReportCreateRequest reportRequest = WeeklyReportCreateRequest.builder()
                .weekNumber(1)
                .content("第一周实习总结：学到了很多知识")
                .build();
        
        WeeklyReportResponse report = internshipService.submitWeeklyReport(internship.getId(), reportRequest);
        assertNotNull(report);
        assertEquals("SUBMITTED", report.getStatus());
        
        // 老师批阅周记
        SessionContext.setCurrentUser(teacher1);
        WeeklyReportReviewRequest reviewRequest = WeeklyReportReviewRequest.builder()
                .status("REVIEWED")
                .teacherComment("表现良好")
                .teacherScore(85)
                .build();
        
        WeeklyReportResponse reviewedReport = internshipService.reviewWeeklyReport(internship.getId(), report.getId(), reviewRequest);
        assertEquals("REVIEWED", reviewedReport.getStatus());
        assertEquals(85, reviewedReport.getTeacherScore());
        
        // 老师提交评价
        EvaluationCreateRequest evalRequest = EvaluationCreateRequest.builder()
                .evaluatorType("TEACHER")
                .score(90)
                .comment("表现优秀")
                .build();
        
        EvaluationResponse evaluation = internshipService.submitEvaluation(internship.getId(), evalRequest);
        assertNotNull(evaluation);
        assertEquals("TEACHER", evaluation.getEvaluatorType());
        assertEquals(90, evaluation.getScore());
        
        // 学生提交对老师的评价
        SessionContext.setCurrentUser(student1);
        EvaluationCreateRequest studentEvalRequest = EvaluationCreateRequest.builder()
                .evaluatorType("STUDENT")
                .score(95)
                .comment("老师教学认真")
                .build();
        
        EvaluationResponse studentEval = internshipService.submitEvaluation(internship.getId(), studentEvalRequest);
        assertNotNull(studentEval);
        assertEquals("STUDENT", studentEval.getEvaluatorType());
    }
    
    /**
     * 测试5: 数据统计准确性
     * 验证学校和医院的统计数据准确
     */
    @Test
    public void testStatisticsAccuracy() {
        // 准备：创建多个岗位和申请
        SessionContext.setCurrentUser(hospitalAdmin1);
        
        // 创建2个岗位
        for (int i = 0; i < 2; i++) {
            PostCreateRequest postRequest = PostCreateRequest.builder()
                    .title("岗位" + i)
                    .department("科室" + i)
                    .description("描述" + i)
                    .quota(5)
                    .duration(30)
                    .visibleSchoolIds(Arrays.asList(school1.getId()))
                    .build();
            
            PostResponse post = postService.createPost(postRequest);
            postService.updatePostStatus(post.getId(), PostStatusUpdateRequest.builder().status("PUBLISHED").build());
        }
        
        // 学生提交申请
        SessionContext.setCurrentUser(student1);
        List<PostResponse> posts = postService.getPostList();
        
        for (PostResponse post : posts) {
            ApplicationCreateRequest appRequest = ApplicationCreateRequest.builder()
                    .postId(post.getId())
                    .reason("申请理由")
                    .build();
            applicationService.submitApplication(appRequest);
        }
        
        // 学校审批
        SessionContext.setCurrentUser(schoolAdmin1);
        List<ApplicationResponse> applications = applicationService.getApplicationList();
        for (ApplicationResponse app : applications) {
            applicationService.schoolReview(app.getId(), ApplicationReviewRequest.builder()
                    .status("APPROVED")
                    .opinion("同意")
                    .build());
        }
        
        // 医院审批
        SessionContext.setCurrentUser(hospitalAdmin1);
        applications = applicationService.getApplicationList();
        for (ApplicationResponse app : applications) {
            applicationService.hospitalReview(app.getId(), ApplicationReviewRequest.builder()
                    .status("APPROVED")
                    .opinion("同意")
                    .build());
        }
        
        // 验证学校统计
        SessionContext.setCurrentUser(schoolAdmin1);
        SchoolStatisticsResponse schoolStats = statisticsService.getSchoolStatistics();
        assertEquals(2L, schoolStats.getTotalApplications());
        assertEquals(2L, schoolStats.getApprovedApplications());
        assertEquals(0L, schoolStats.getRejectedApplications());
        
        // 验证医院统计
        SessionContext.setCurrentUser(hospitalAdmin1);
        HospitalStatisticsResponse hospitalStats = statisticsService.getHospitalStatistics();
        assertEquals(2L, hospitalStats.getTotalPosts());
        assertEquals(2L, hospitalStats.getTotalApplications());
        assertEquals(2L, hospitalStats.getApprovedApplications());
    }
    
    /**
     * 测试6: 多学校多医院的数据隔离
     * 验证不同学校和医院的数据完全隔离
     */
    @Test
    public void testMultiSchoolMultiHospitalDataIsolation() {
        // 医院1为学校1创建岗位
        SessionContext.setCurrentUser(hospitalAdmin1);
        PostCreateRequest post1Request = PostCreateRequest.builder()
                .title("医院1的岗位")
                .department("科室1")
                .description("描述1")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(school1.getId()))
                .build();
        
        PostResponse post1 = postService.createPost(post1Request);
        postService.updatePostStatus(post1.getId(), PostStatusUpdateRequest.builder().status("PUBLISHED").build());
        
        // 医院2为学校2创建岗位
        SessionContext.setCurrentUser(hospitalAdmin2);
        PostCreateRequest post2Request = PostCreateRequest.builder()
                .title("医院2的岗位")
                .department("科室2")
                .description("描述2")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(school2.getId()))
                .build();
        
        PostResponse post2 = postService.createPost(post2Request);
        postService.updatePostStatus(post2.getId(), PostStatusUpdateRequest.builder().status("PUBLISHED").build());
        
        // 学生1只能看到医院1的岗位
        SessionContext.setCurrentUser(student1);
        List<PostResponse> student1Posts = postService.getPostList();
        assertTrue(student1Posts.stream().anyMatch(p -> p.getId().equals(post1.getId())));
        assertFalse(student1Posts.stream().anyMatch(p -> p.getId().equals(post2.getId())));
        
        // 学生2只能看到医院2的岗位
        SessionContext.setCurrentUser(student2);
        List<PostResponse> student2Posts = postService.getPostList();
        assertFalse(student2Posts.stream().anyMatch(p -> p.getId().equals(post1.getId())));
        assertTrue(student2Posts.stream().anyMatch(p -> p.getId().equals(post2.getId())));
        
        // 医院1的管理员只能看到医院1的岗位
        SessionContext.setCurrentUser(hospitalAdmin1);
        HospitalStatisticsResponse hospital1Stats = statisticsService.getHospitalStatistics();
        assertEquals(1L, hospital1Stats.getTotalPosts());
        
        // 医院2的管理员只能看到医院2的岗位
        SessionContext.setCurrentUser(hospitalAdmin2);
        HospitalStatisticsResponse hospital2Stats = statisticsService.getHospitalStatistics();
        assertEquals(1L, hospital2Stats.getTotalPosts());
    }
    
    /**
     * 辅助方法：将User实体转换为UserResponse
     */
    private UserResponse convertUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .organization(OrganizationResponse.builder()
                        .id(user.getOrganization().getId())
                        .name(user.getOrganization().getName())
                        .type(user.getOrganization().getType())
                        .code(user.getOrganization().getCode())
                        .build())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
