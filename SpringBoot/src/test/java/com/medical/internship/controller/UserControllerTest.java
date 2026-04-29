package com.medical.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.UserLoginRequest;
import com.medical.internship.dto.UserRegisterRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    private UserResponse testUserResponse;
    private OrganizationResponse testOrgResponse;
    
    @BeforeEach
    public void setUp() {
        testOrgResponse = OrganizationResponse.builder()
                .id(1L)
                .name("测试学校")
                .type("SCHOOL")
                .code("TEST_SCHOOL_001")
                .build();
        
        testUserResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("STUDENT")
                .status("APPROVED")
                .organization(testOrgResponse)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    public void testRegisterSuccess() throws Exception {
        // 准备测试数据
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("newuser")
                .password("password123")
                .email("newuser@example.com")
                .role("STUDENT")
                .organizationId(1L)
                .build();
        
        // 模拟service行为
        when(userService.register(any(UserRegisterRequest.class))).thenReturn(testUserResponse);
        
        // 执行测试
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }
    
    @Test
    public void testLoginSuccess() throws Exception {
        // 准备测试数据
        UserLoginRequest request = UserLoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
        
        // 模拟service行为
        when(userService.login(anyString(), anyString())).thenReturn(testUserResponse);
        
        // 执行测试
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.organization.id").value(1));
    }
    
    @Test
    public void testGetProfileWithoutSession() throws Exception {
        // 执行测试 - 没有会话应该返回401
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    public void testGetProfileWithSession() throws Exception {
        // 准备会话
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", testUserResponse);
        
        // 模拟service行为
        when(userService.getUserProfile(1L)).thenReturn(testUserResponse);
        
        // 执行测试
        mockMvc.perform(get("/api/users/profile")
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }
    
    @Test
    public void testRegisterMissingUsername() throws Exception {
        // 准备测试数据 - 缺少用户名
        UserRegisterRequest request = UserRegisterRequest.builder()
                .password("password123")
                .email("newuser@example.com")
                .role("STUDENT")
                .organizationId(1L)
                .build();
        
        // 执行测试 - 应该返回400
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
