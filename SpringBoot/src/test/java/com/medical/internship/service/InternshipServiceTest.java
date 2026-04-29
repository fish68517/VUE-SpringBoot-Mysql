package com.medical.internship.service;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.*;
import com.medical.internship.entity.*;
import com.medical.internship.repository.*;
import com.medical.internship.service.impl.InternshipServiceImpl;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 实习过程管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    
    @Mock
    private InternshipRepository internshipRepository;
    
    @Mock
    private WeeklyReportRepository weeklyReportRepository;
    
    @Mock
    private EvaluationRepository evaluationRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private InternshipServiceImpl internshipService;
    
    private Organization testSchool;
    private Organization testHospital;
    private User testStudent;
    private User testTeacher;
    private Post testPost;
    private Application testApplication;
    private Internship testInternship;
    private WeeklyReport testWeeklyReport;
    private Evaluation testEvaluation;
    private UserResponse studentUserResponse;
    private UserResponse teacherUserResponse;
    
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
                .schoolStatus("APPROVED")
                .hospitalStatus("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testInternship = Internship.builder()
                .id(1L)
                .application(testApplication)
                .student(testStudent)
                .teacher(testTeacher)
                .post(testPost)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .status("ONGOING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testWeeklyReport = WeeklyReport.builder()
                .id(1L)
                .internship(testInternship)
                .weekNumber(1)
                .content("第一周实习总结")
                .status("SUBMITTED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testEvaluation = Evaluation.builder()
                .id(1L)
                .internship(testInternship)
                .evaluator(testTeacher)
                .evaluatorType("TEACHER")
                .score(85)
                .comment("表现良好")
                .createdAt(LocalDateTime.now())
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
        
        teacherUserResponse = UserResponse.builder()
                .id(2L)
                .username("teacher")
                .email("teacher@test.com")
                .role("TEACHER")
                .organization(OrganizationResponse.builder()
                        .id(2L)
                        .name("测试医院")
                        .type("HOSPITAL")
                        .code("TEST_HOSPITAL_001")
                        .build())
                .build();
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    @Test
    public void testGetInternshipListByStudent() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(internshipRepository.findByStudentId(1L)).thenReturn(Arrays.asList(testInternship));
        
        // 执行测试
        List<InternshipResponse> responses = internshipService.getInternshipList();
        
        // 验证结果
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findByStudentId(1L);
    }
    
    @Test
    public void testGetInternshipListByTeacher() {
        // 设置会话上下文
        SessionContext.setCurrentUser(teacherUserResponse);
        
        // 模拟repository行为
        when(internshipRepository.findByTeacherId(2L)).thenReturn(Arrays.asList(testInternship));
        
        // 执行测试
        List<InternshipResponse> responses = internshipService.getInternshipList();
        
        // 验证结果
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findByTeacherId(2L);
    }
    
    @Test
    public void testGetInternshipDetailSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        
        // 执行测试
        InternshipResponse response = internshipService.getInternshipDetail(1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ONGOING", response.getStatus());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetInternshipDetailNotFound() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(internshipRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> internshipService.getInternshipDetail(999L));
    }
    
    @Test
    public void testSubmitWeeklyReportSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        WeeklyReportCreateRequest request = WeeklyReportCreateRequest.builder()
                .weekNumber(1)
                .content("第一周实习总结")
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(weeklyReportRepository.findByInternshipIdAndWeekNumber(1L, 1)).thenReturn(Optional.empty());
        when(weeklyReportRepository.save(any(WeeklyReport.class))).thenReturn(testWeeklyReport);
        
        // 执行测试
        WeeklyReportResponse response = internshipService.submitWeeklyReport(1L, request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1, response.getWeekNumber());
        assertEquals("第一周实习总结", response.getContent());
        assertEquals("SUBMITTED", response.getStatus());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
        verify(weeklyReportRepository, times(1)).save(any(WeeklyReport.class));
    }
    
    @Test
    public void testSubmitWeeklyReportDuplicate() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        WeeklyReportCreateRequest request = WeeklyReportCreateRequest.builder()
                .weekNumber(1)
                .content("第一周实习总结")
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(weeklyReportRepository.findByInternshipIdAndWeekNumber(1L, 1))
                .thenReturn(Optional.of(testWeeklyReport));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> internshipService.submitWeeklyReport(1L, request));
    }
    
    @Test
    public void testGetWeeklyReportListSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(weeklyReportRepository.findByInternshipId(1L)).thenReturn(Arrays.asList(testWeeklyReport));
        
        // 执行测试
        List<WeeklyReportResponse> responses = internshipService.getWeeklyReportList(1L);
        
        // 验证结果
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1, responses.get(0).getWeekNumber());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
        verify(weeklyReportRepository, times(1)).findByInternshipId(1L);
    }
    
    @Test
    public void testReviewWeeklyReportSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(teacherUserResponse);
        
        // 准备测试数据
        WeeklyReportReviewRequest request = WeeklyReportReviewRequest.builder()
                .status("REVIEWED")
                .teacherComment("表现良好")
                .teacherScore(85)
                .build();
        
        WeeklyReport reviewedReport = WeeklyReport.builder()
                .id(1L)
                .internship(testInternship)
                .weekNumber(1)
                .content("第一周实习总结")
                .teacherComment("表现良好")
                .teacherScore(85)
                .status("REVIEWED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(weeklyReportRepository.findById(1L)).thenReturn(Optional.of(testWeeklyReport));
        when(weeklyReportRepository.save(any(WeeklyReport.class))).thenReturn(reviewedReport);
        
        // 执行测试
        WeeklyReportResponse response = internshipService.reviewWeeklyReport(1L, 1L, request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("REVIEWED", response.getStatus());
        assertEquals(85, response.getTeacherScore());
        assertEquals("表现良好", response.getTeacherComment());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
        verify(weeklyReportRepository, times(1)).findById(1L);
        verify(weeklyReportRepository, times(1)).save(any(WeeklyReport.class));
    }
    
    @Test
    public void testReviewWeeklyReportInvalidScore() {
        // 设置会话上下文
        SessionContext.setCurrentUser(teacherUserResponse);
        
        // 准备测试数据
        WeeklyReportReviewRequest request = WeeklyReportReviewRequest.builder()
                .status("REVIEWED")
                .teacherComment("表现良好")
                .teacherScore(150)
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(weeklyReportRepository.findById(1L)).thenReturn(Optional.of(testWeeklyReport));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> internshipService.reviewWeeklyReport(1L, 1L, request));
    }
    
    @Test
    public void testSubmitEvaluationByTeacher() {
        // 设置会话上下文
        SessionContext.setCurrentUser(teacherUserResponse);
        
        // 准备测试数据
        EvaluationCreateRequest request = EvaluationCreateRequest.builder()
                .evaluatorType("TEACHER")
                .score(85)
                .comment("表现良好")
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testTeacher));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(testEvaluation);
        
        // 执行测试
        EvaluationResponse response = internshipService.submitEvaluation(1L, request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("TEACHER", response.getEvaluatorType());
        assertEquals(85, response.getScore());
        assertEquals("表现良好", response.getComment());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }
    
    @Test
    public void testSubmitEvaluationByStudent() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 准备测试数据
        EvaluationCreateRequest request = EvaluationCreateRequest.builder()
                .evaluatorType("STUDENT")
                .score(90)
                .comment("老师教学认真")
                .build();
        
        Evaluation studentEvaluation = Evaluation.builder()
                .id(2L)
                .internship(testInternship)
                .evaluator(testStudent)
                .evaluatorType("STUDENT")
                .score(90)
                .comment("老师教学认真")
                .createdAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(studentEvaluation);
        
        // 执行测试
        EvaluationResponse response = internshipService.submitEvaluation(1L, request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("STUDENT", response.getEvaluatorType());
        assertEquals(90, response.getScore());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }
    
    @Test
    public void testSubmitEvaluationInvalidScore() {
        // 设置会话上下文
        SessionContext.setCurrentUser(teacherUserResponse);
        
        // 准备测试数据
        EvaluationCreateRequest request = EvaluationCreateRequest.builder()
                .evaluatorType("TEACHER")
                .score(150)
                .comment("表现良好")
                .build();
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> internshipService.submitEvaluation(1L, request));
    }
    
    @Test
    public void testGetEvaluationListSuccess() {
        // 设置会话上下文
        SessionContext.setCurrentUser(studentUserResponse);
        
        // 模拟repository行为
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(testInternship));
        when(evaluationRepository.findByInternshipId(1L)).thenReturn(Arrays.asList(testEvaluation));
        
        // 执行测试
        List<EvaluationResponse> responses = internshipService.getEvaluationList(1L);
        
        // 验证结果
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("TEACHER", responses.get(0).getEvaluatorType());
        
        // 验证方法调用
        verify(internshipRepository, times(1)).findById(1L);
        verify(evaluationRepository, times(1)).findByInternshipId(1L);
    }
}
