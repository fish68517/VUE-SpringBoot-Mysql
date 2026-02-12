package com.medical.internship.service;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.ApplicationCreateRequest;
import com.medical.internship.dto.ApplicationResponse;
import com.medical.internship.dto.ApplicationReviewRequest;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.entity.Application;
import com.medical.internship.entity.Internship;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.Post;
import com.medical.internship.entity.User;
import com.medical.internship.repository.ApplicationRepository;
import com.medical.internship.repository.InternshipRepository;
import com.medical.internship.repository.PostRepository;
import com.medical.internship.repository.UserRepository;
import com.medical.internship.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 申请服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private PostRepository postRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private InternshipRepository internshipRepository;
    
    @Mock
    private PostService postService;
    
    @InjectMocks
    private ApplicationServiceImpl applicationService;
    
    private Organization testSchool;
    private Organization testHospital;
    private User testStudent;
    private User testTeacher;
    private Post testPost;
    private Application testApplication;
    private UserResponse studentUserResponse;
    
    @BeforeEach
    public void setUp() {
        testSchool = Organization.builder()
                .id(1L)
                .name("测试学校")
                .type("SCHOOL")
                .code("TEST_SCHOOL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        testHospital = Organization.builder()
                .id(2L)
                .name("测试医院")
                .type("HOSPITAL")
                .code("TEST_HOSPITAL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        testStudent = User.builder()
                .id(1L)
                .username("student")
                .password("password")
                .email("student@test.com")
                .role("STUDENT")
                .organization(testSchool)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testTeacher = User.builder()
                .id(2L)
                .username("teacher")
                .password("password")
                .email("teacher@test.com")
                .role("TEACHER")
                .organization(testHospital)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testPost = Post.builder()
                .id(1L)
                .hospital(testHospital)
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchools("[1]")
                .status("PUBLISHED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testApplication = Application.builder()
                .id(1L)
                .student(testStudent)
                .post(testPost)
                .reason("我想在内科实习")
                .schoolStatus("PENDING")
                .hospitalStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        studentUserResponse = UserResponse.builder()
                .id(1L)
                .username("student")
                .email("student@test.com")
                .role("STUDENT")
                .organization(OrganizationResponse.builder()
                        .id(1L)
                        .name("测试学校")
                        .type("SCHOOL")
                        .code("TEST_SCHOOL_001")
                        .build())
                .build();
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    @Test
    public void testSubmitApplicationSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .postId(1L)
                .reason("我想在内科实习")
                .build();
        
        // 模拟repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(applicationRepository.findByStudentIdAndPostId(1L, 1L)).thenReturn(new ArrayList<>());
        when(postService.isPostQuotaFull(1L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        
        // 执行测试
        ApplicationResponse response = applicationService.submitApplication(request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("我想在内科实习", response.getReason());
        assertEquals("PENDING", response.getSchoolStatus());
        assertEquals("PENDING", response.getHospitalStatus());
        
        // 验证方法调用
        verify(userRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).save(any(Application.class));
    }
    
    @Test
    public void testSubmitApplicationPostNotPublished() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        Post archivedPost = Post.builder()
                .id(1L)
                .status("ARCHIVED")
                .build();
        
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .postId(1L)
                .reason("我想在内科实习")
                .build();
        
        // 模拟repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(postRepository.findById(1L)).thenReturn(Optional.of(archivedPost));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> applicationService.submitApplication(request));
    }
    
    @Test
    public void testSubmitApplicationQuotaFull() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .postId(1L)
                .reason("我想在内科实习")
                .build();
        
        // 模拟repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postService.isPostQuotaFull(1L)).thenReturn(true);
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> applicationService.submitApplication(request));
    }
    
    @Test
    public void testSubmitApplicationDuplicate() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .postId(1L)
                .reason("我想在内科实习")
                .build();
        
        // 模拟repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(postService.isPostQuotaFull(1L)).thenReturn(false);
        when(applicationRepository.findByStudentIdAndPostId(1L, 1L))
                .thenReturn(Arrays.asList(testApplication));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> applicationService.submitApplication(request));
    }
    
    @Test
    public void testGetApplicationDetailSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        
        // 执行测试
        ApplicationResponse response = applicationService.getApplicationDetail(1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("我想在内科实习", response.getReason());
        
        // 验证方法调用
        verify(applicationRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetApplicationDetailNotFound() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> applicationService.getApplicationDetail(999L));
    }
    
    @Test
    public void testSchoolReviewSuccess() {
        // 设置会话上下文 - 学校管理员
        UserResponse schoolAdminResponse = UserResponse.builder()
                .id(3L)
                .username("school_admin")
                .role("SCHOOL_ADMIN")
                .organization(OrganizationResponse.builder()
                        .id(1L)
                        .name("测试学校")
                        .type("SCHOOL")
                        .code("TEST_SCHOOL_001")
                        .build())
                .build();
        
        SessionContext.setCurrentUser(schoolAdminResponse);
        
        // 准备测试数据
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        Application updatedApplication = Application.builder()
                .id(1L)
                .student(testStudent)
                .post(testPost)
                .reason("我想在内科实习")
                .schoolStatus("APPROVED")
                .hospitalStatus("PENDING")
                .schoolOpinion("同意申请")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(updatedApplication);
        
        // 执行测试
        ApplicationResponse response = applicationService.schoolReview(1L, request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("APPROVED", response.getSchoolStatus());
        assertEquals("同意申请", response.getSchoolOpinion());
        
        // 验证方法调用
        verify(applicationRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).save(any(Application.class));
    }
    
    @Test
    public void testHospitalReviewSuccess() {
        // 设置会话上下文 - 医院管理员
        UserResponse hospitalAdminResponse = UserResponse.builder()
                .id(4L)
                .username("hospital_admin")
                .role("HOSPITAL_ADMIN")
                .organization(OrganizationResponse.builder()
                        .id(2L)
                        .name("测试医院")
                        .type("HOSPITAL")
                        .code("TEST_HOSPITAL_001")
                        .build())
                .build();
        
        SessionContext.setCurrentUser(hospitalAdminResponse);
        
        // 准备测试数据
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        Application approvedBySchool = Application.builder()
                .id(1L)
                .student(testStudent)
                .post(testPost)
                .reason("我想在内科实习")
                .schoolStatus("APPROVED")
                .hospitalStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Application updatedApplication = Application.builder()
                .id(1L)
                .student(testStudent)
                .post(testPost)
                .reason("我想在内科实习")
                .schoolStatus("APPROVED")
                .hospitalStatus("APPROVED")
                .hospitalOpinion("同意申请")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(approvedBySchool));
        when(userRepository.findByOrganizationIdAndRole(2L, "TEACHER"))
                .thenReturn(Arrays.asList(testTeacher));
        when(applicationRepository.save(any(Application.class))).thenReturn(updatedApplication);
        when(internshipRepository.save(any(Internship.class))).thenReturn(null);
        
        // 执行测试
        ApplicationResponse response = applicationService.hospitalReview(1L, request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("APPROVED", response.getHospitalStatus());
        assertEquals("同意申请", response.getHospitalOpinion());
        
        // 验证方法调用
        verify(applicationRepository, times(1)).findById(1L);
        verify(applicationRepository, times(1)).save(any(Application.class));
        verify(internshipRepository, times(1)).save(any(Internship.class));
    }
    
    @Test
    public void testHospitalReviewNotApprovedBySchool() {
        // 设置会话上下文 - 医院管理员
        UserResponse hospitalAdminResponse = UserResponse.builder()
                .id(4L)
                .username("hospital_admin")
                .role("HOSPITAL_ADMIN")
                .organization(OrganizationResponse.builder()
                        .id(2L)
                        .name("测试医院")
                        .type("HOSPITAL")
                        .code("TEST_HOSPITAL_001")
                        .build())
                .build();
        
        SessionContext.setCurrentUser(hospitalAdminResponse);
        
        // 准备测试数据
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        // 模拟repository行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> applicationService.hospitalReview(1L, request));
    }
}
