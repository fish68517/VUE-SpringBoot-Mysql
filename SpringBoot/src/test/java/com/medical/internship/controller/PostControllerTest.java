package com.medical.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.PostCreateRequest;
import com.medical.internship.dto.PostResponse;
import com.medical.internship.dto.PostStatusUpdateRequest;
import com.medical.internship.dto.PostUpdateRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 岗位控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private PostService postService;
    
    private PostResponse testPostResponse;
    private OrganizationResponse testHospitalResponse;
    private UserResponse hospitalAdminUser;
    private MockHttpSession session;
    
    @BeforeEach
    public void setUp() {
        testHospitalResponse = OrganizationResponse.builder()
                .id(1L)
                .name("测试医院")
                .type("HOSPITAL")
                .code("TEST_HOSPITAL_001")
                .build();
        
        testPostResponse = PostResponse.builder()
                .id(1L)
                .hospital(testHospitalResponse)
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(2L))
                .status("PUBLISHED")
                .applicationCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        hospitalAdminUser = UserResponse.builder()
                .id(1L)
                .username("hospital_admin")
                .role("HOSPITAL_ADMIN")
                .organization(testHospitalResponse)
                .build();
        
        session = new MockHttpSession();
        session.setAttribute("currentUser", hospitalAdminUser);
    }
    
    @Test
    public void testGetPostListSuccess() throws Exception {
        // 准备测试数据
        List<PostResponse> posts = Arrays.asList(testPostResponse);
        
        // 模拟service行为
        when(postService.getPostList()).thenReturn(posts);
        
        // 执行测试
        mockMvc.perform(get("/api/posts")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].title").value("内科实习岗位"))
                .andExpect(jsonPath("$.data[0].quota").value(5));
    }
    
    @Test
    public void testGetPostDetailSuccess() throws Exception {
        // 模拟service行为
        when(postService.getPostDetail(1L)).thenReturn(testPostResponse);
        
        // 执行测试
        mockMvc.perform(get("/api/posts/1")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("内科实习岗位"))
                .andExpect(jsonPath("$.data.department").value("内科"));
    }
    
    @Test
    public void testCreatePostSuccess() throws Exception {
        // 准备测试数据
        PostCreateRequest request = PostCreateRequest.builder()
                .title("内科实习岗位")
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .visibleSchoolIds(Arrays.asList(2L))
                .build();
        
        // 模拟service行为
        when(postService.createPost(any(PostCreateRequest.class))).thenReturn(testPostResponse);
        
        // 执行测试
        mockMvc.perform(post("/api/posts")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("岗位创建成功"))
                .andExpect(jsonPath("$.data.title").value("内科实习岗位"));
    }
    
    @Test
    public void testCreatePostMissingTitle() throws Exception {
        // 准备测试数据 - 缺少岗位名称
        PostCreateRequest request = PostCreateRequest.builder()
                .department("内科")
                .description("内科实习岗位描述")
                .quota(5)
                .duration(30)
                .build();
        
        // 执行测试 - 应该返回400
        mockMvc.perform(post("/api/posts")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void testUpdatePostSuccess() throws Exception {
        // 准备测试数据
        PostUpdateRequest request = PostUpdateRequest.builder()
                .title("更新后的岗位名称")
                .quota(10)
                .build();
        
        // 模拟service行为
        when(postService.updatePost(anyLong(), any(PostUpdateRequest.class))).thenReturn(testPostResponse);
        
        // 执行测试
        mockMvc.perform(put("/api/posts/1")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("岗位更新成功"));
    }
    
    @Test
    public void testUpdatePostStatusSuccess() throws Exception {
        // 准备测试数据
        PostStatusUpdateRequest request = PostStatusUpdateRequest.builder()
                .status("ARCHIVED")
                .build();
        
        // 模拟service行为
        when(postService.updatePostStatus(anyLong(), any(PostStatusUpdateRequest.class)))
                .thenReturn(testPostResponse);
        
        // 执行测试
        mockMvc.perform(put("/api/posts/1/status")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("岗位状态更新成功"));
    }
    
    @Test
    public void testGetPostListWithoutSession() throws Exception {
        // 执行测试 - 没有会话应该返回401
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isUnauthorized());
    }
}
