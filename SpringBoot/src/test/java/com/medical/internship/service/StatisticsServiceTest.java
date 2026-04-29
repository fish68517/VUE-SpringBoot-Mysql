package com.medical.internship.service;

import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.HospitalStatisticsResponse;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.SchoolStatisticsResponse;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.entity.Application;
import com.medical.internship.entity.Internship;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.Post;
import com.medical.internship.entity.User;
import com.medical.internship.repository.ApplicationRepository;
import com.medical.internship.repository.InternshipRepository;
import com.medical.internship.repository.PostRepository;
import com.medical.internship.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 数据统计服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {
    
    @Mock
    private ApplicationRepository applicationRepository;
    
    @Mock
    private InternshipRepository internshipRepository;
    
    @Mock
    private PostRepository postRepository;
    
    @InjectMocks
    private StatisticsServiceImpl statisticsService;
    
    private Organization testSchool;
    private Organization testHospital;
    private User schoolAdmin;
    private User hospitalAdmin;
    private UserResponse schoolAdminResponse;
    private UserResponse hospitalAdminResponse;
    
    @BeforeEach
    public void setUp() {
        testSchool = Organization.builder()
                .id(1L)
                .name("测试学校")
                .type("SCHOOL")
                .code("SCHOOL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        testHospital = Organization.builder()
                .id(2L)
                .name("测试医院")
                .type("HOSPITAL")
                .code("HOSPITAL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        schoolAdmin = User.builder()
                .id(1L)
                .username("school_admin")
                .role("SCHOOL_ADMIN")
                .organization(testSchool)
                .build();
        
        hospitalAdmin = User.builder()
                .id(2L)
                .username("hospital_admin")
                .role("HOSPITAL_ADMIN")
                .organization(testHospital)
                .build();
        
        schoolAdminResponse = UserResponse.builder()
                .id(1L)
                .username("school_admin")
                .role("SCHOOL_ADMIN")
                .organization(OrganizationResponse.builder()
                        .id(1L)
                        .name("测试学校")
                        .type("SCHOOL")
                        .code("SCHOOL_001")
                        .build())
                .build();
        
        hospitalAdminResponse = UserResponse.builder()
                .id(2L)
                .username("hospital_admin")
                .role("HOSPITAL_ADMIN")
                .organization(OrganizationResponse.builder()
                        .id(2L)
                        .name("测试医院")
                        .type("HOSPITAL")
                        .code("HOSPITAL_001")
                        .build())
                .build();
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    @Test
    public void testGetSchoolStatisticsSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(schoolAdminResponse);
        
        // 准备测试数据 - 学生
        User student1 = User.builder()
                .id(10L)
                .username("student1")
                .role("STUDENT")
                .organization(testSchool)
                .build();
        
        User student2 = User.builder()
                .id(11L)
                .username("student2")
                .role("STUDENT")
                .organization(testSchool)
                .build();
        
        // 准备测试数据 - 申请
        Application app1 = Application.builder()
                .id(1L)
                .student(student1)
                .schoolStatus("APPROVED")
                .hospitalStatus("APPROVED")
                .build();
        
        Application app2 = Application.builder()
                .id(2L)
                .student(student2)
                .schoolStatus("APPROVED")
                .hospitalStatus("REJECTED")
                .build();
        
        // 准备测试数据 - 实习记录
        Post post1 = Post.builder()
                .id(1L)
                .hospital(testHospital)
                .build();
        
        Internship internship1 = Internship.builder()
                .id(1L)
                .student(student1)
                .post(post1)
                .status("ONGOING")
                .build();
        
        Internship internship2 = Internship.builder()
                .id(2L)
                .student(student2)
                .post(post1)
                .status("COMPLETED")
                .build();
        
        // 模拟repository行为
        when(applicationRepository.findAll()).thenReturn(Arrays.asList(app1, app2));
        when(internshipRepository.findAll()).thenReturn(Arrays.asList(internship1, internship2));
        
        // 执行测试
        SchoolStatisticsResponse response = statisticsService.getSchoolStatistics();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(2L, response.getTotalApplications());
        assertEquals(1L, response.getApprovedApplications());
        assertEquals(1L, response.getRejectedApplications());
        assertEquals(1L, response.getOngoingInternships());
        assertEquals(1L, response.getCompletedInternships());
        assertEquals(100.0, response.getCompletionRate());
        
        // 验证方法调用
        verify(applicationRepository, times(1)).findAll();
        verify(internshipRepository, times(1)).findAll();
    }
    
    @Test
    public void testGetSchoolStatisticsWithZeroApprovedApplications() {
        // 设置会话上下文
        SessionContext.setCurrentUser(schoolAdminResponse);
        
        // 准备测试数据 - 学生
        User student1 = User.builder()
                .id(10L)
                .username("student1")
                .role("STUDENT")
                .organization(testSchool)
                .build();
        
        // 准备测试数据 - 申请（全部驳回）
        Application app1 = Application.builder()
                .id(1L)
                .student(student1)
                .schoolStatus("APPROVED")
                .hospitalStatus("REJECTED")
                .build();
        
        // 模拟repository行为
        when(applicationRepository.findAll()).thenReturn(Arrays.asList(app1));
        when(internshipRepository.findAll()).thenReturn(new ArrayList<>());
        
        // 执行测试
        SchoolStatisticsResponse response = statisticsService.getSchoolStatistics();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getTotalApplications());
        assertEquals(0L, response.getApprovedApplications());
        assertEquals(1L, response.getRejectedApplications());
        assertEquals(0.0, response.getCompletionRate());
    }
    
    @Test
    public void testGetHospitalStatisticsSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminResponse);
        
        // 准备测试数据 - 岗位
        Post post1 = Post.builder()
                .id(1L)
                .hospital(testHospital)
                .title("岗位1")
                .build();
        
        Post post2 = Post.builder()
                .id(2L)
                .hospital(testHospital)
                .title("岗位2")
                .build();
        
        // 准备测试数据 - 学生
        User student1 = User.builder()
                .id(10L)
                .username("student1")
                .role("STUDENT")
                .organization(testSchool)
                .build();
        
        User student2 = User.builder()
                .id(11L)
                .username("student2")
                .role("STUDENT")
                .organization(testSchool)
                .build();
        
        // 准备测试数据 - 申请
        Application app1 = Application.builder()
                .id(1L)
                .student(student1)
                .post(post1)
                .hospitalStatus("APPROVED")
                .build();
        
        Application app2 = Application.builder()
                .id(2L)
                .student(student2)
                .post(post2)
                .hospitalStatus("REJECTED")
                .build();
        
        // 准备测试数据 - 实习记录
        User teacher = User.builder()
                .id(20L)
                .username("teacher")
                .role("TEACHER")
                .organization(testHospital)
                .build();
        
        Internship internship1 = Internship.builder()
                .id(1L)
                .student(student1)
                .teacher(teacher)
                .post(post1)
                .status("ONGOING")
                .build();
        
        // 模拟repository行为
        when(postRepository.findByHospitalId(2L)).thenReturn(Arrays.asList(post1, post2));
        when(applicationRepository.findByPostId(1L)).thenReturn(Arrays.asList(app1));
        when(applicationRepository.findByPostId(2L)).thenReturn(Arrays.asList(app2));
        when(internshipRepository.findByPostId(1L)).thenReturn(Arrays.asList(internship1));
        when(internshipRepository.findByPostId(2L)).thenReturn(new ArrayList<>());
        
        // 执行测试
        HospitalStatisticsResponse response = statisticsService.getHospitalStatistics();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(2L, response.getTotalPosts());
        assertEquals(2L, response.getTotalApplications());
        assertEquals(1L, response.getApprovedApplications());
        assertEquals(1L, response.getRejectedApplications());
        assertEquals(1L, response.getOngoingInternships());
        assertEquals(0L, response.getCompletedInternships());
        
        // 验证方法调用
        verify(postRepository, times(1)).findByHospitalId(2L);
        verify(applicationRepository, times(2)).findByPostId(anyLong());
        verify(internshipRepository, times(2)).findByPostId(anyLong());
    }
    
    @Test
    public void testGetHospitalStatisticsWithNoPosts() {
        // 设置会话上下文
        SessionContext.setCurrentUser(hospitalAdminResponse);
        
        // 模拟repository行为 - 没有岗位
        when(postRepository.findByHospitalId(2L)).thenReturn(new ArrayList<>());
        
        // 执行测试
        HospitalStatisticsResponse response = statisticsService.getHospitalStatistics();
        
        // 验证结果
        assertNotNull(response);
        assertEquals(0L, response.getTotalPosts());
        assertEquals(0L, response.getTotalApplications());
        assertEquals(0L, response.getApprovedApplications());
        assertEquals(0L, response.getOngoingInternships());
        assertEquals(0L, response.getCompletedInternships());
    }
}
