package com.medical.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.ApplicationCreateRequest;
import com.medical.internship.dto.ApplicationResponse;
import com.medical.internship.dto.ApplicationReviewRequest;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.PostResponse;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 申请控制器测试类
 */
@ExtendWith(MockitoExtension.class)
public class ApplicationControllerTest {
    
    @Mock
    private ApplicationService applicationService;
    
    @InjectMocks
    private ApplicationController applicationController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    private ApplicationResponse testApplicationResponse;
    private UserResponse testStudentResponse;
    private PostResponse testPostResponse;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
        objectMapper = new ObjectMapper();
        
        testStudentResponse = UserResponse.builder()
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
        
        testPostResponse = PostResponse.builder()
                .id(1L)
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .status("PUBLISHED")
                .build();
        
        testApplicationResponse = ApplicationResponse.builder()
                .id(1L)
                .student(testStudentResponse)
                .post(testPostResponse)
                .reason("我想在内科实习")
                .schoolStatus("PENDING")
                .hospitalStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    public void testSubmitApplicationSuccess() throws Exception {
        // 准备测试数据
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .postId(1L)
                .reason("我想在内科实习")
                .build();
        
        // 模拟service行为
        when(applicationService.submitApplication(any(ApplicationCreateRequest.class)))
                .thenReturn(testApplicationResponse);
        
        // 执行测试
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("申请提交成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.reason").value("我想在内科实习"));
    }
    
    @Test
    public void testSubmitApplicationMissingPostId() throws Exception {
        // 准备测试数据 - 缺少postId
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .reason("我想在内科实习")
                .build();
        
        // 执行测试
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testSubmitApplicationMissingReason() throws Exception {
        // 准备测试数据 - 缺少reason
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .postId(1L)
                .build();
        
        // 执行测试
        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testGetApplicationListSuccess() throws Exception {
        // 准备测试数据
        List<ApplicationResponse> applications = Arrays.asList(testApplicationResponse);
        
        // 模拟service行为
        when(applicationService.getApplicationList()).thenReturn(applications);
        
        // 执行测试
        mockMvc.perform(get("/api/applications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].reason").value("我想在内科实习"));
    }
    
    @Test
    public void testGetApplicationDetailSuccess() throws Exception {
        // 模拟service行为
        when(applicationService.getApplicationDetail(1L)).thenReturn(testApplicationResponse);
        
        // 执行测试
        mockMvc.perform(get("/api/applications/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.reason").value("我想在内科实习"));
    }
    
    @Test
    public void testSchoolReviewSuccess() throws Exception {
        // 准备测试数据
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        ApplicationResponse approvedResponse = ApplicationResponse.builder()
                .id(1L)
                .student(testStudentResponse)
                .post(testPostResponse)
                .reason("我想在内科实习")
                .schoolStatus("APPROVED")
                .hospitalStatus("PENDING")
                .schoolOpinion("同意申请")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟service行为
        when(applicationService.schoolReview(eq(1L), any(ApplicationReviewRequest.class)))
                .thenReturn(approvedResponse);
        
        // 执行测试
        mockMvc.perform(put("/api/applications/1/school-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("学校审批完成"))
                .andExpect(jsonPath("$.data.schoolStatus").value("APPROVED"))
                .andExpect(jsonPath("$.data.schoolOpinion").value("同意申请"));
    }
    
    @Test
    public void testSchoolReviewMissingStatus() throws Exception {
        // 准备测试数据 - 缺少status
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .opinion("同意申请")
                .build();
        
        // 执行测试
        mockMvc.perform(put("/api/applications/1/school-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testHospitalReviewSuccess() throws Exception {
        // 准备测试数据
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .status("APPROVED")
                .opinion("同意申请")
                .build();
        
        ApplicationResponse approvedResponse = ApplicationResponse.builder()
                .id(1L)
                .student(testStudentResponse)
                .post(testPostResponse)
                .reason("我想在内科实习")
                .schoolStatus("APPROVED")
                .hospitalStatus("APPROVED")
                .hospitalOpinion("同意申请")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟service行为
        when(applicationService.hospitalReview(eq(1L), any(ApplicationReviewRequest.class)))
                .thenReturn(approvedResponse);
        
        // 执行测试
        mockMvc.perform(put("/api/applications/1/hospital-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("医院审批完成"))
                .andExpect(jsonPath("$.data.hospitalStatus").value("APPROVED"))
                .andExpect(jsonPath("$.data.hospitalOpinion").value("同意申请"));
    }
    
    @Test
    public void testHospitalReviewMissingStatus() throws Exception {
        // 准备测试数据 - 缺少status
        ApplicationReviewRequest request = ApplicationReviewRequest.builder()
                .opinion("同意申请")
                .build();
        
        // 执行测试
        mockMvc.perform(put("/api/applications/1/hospital-review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
