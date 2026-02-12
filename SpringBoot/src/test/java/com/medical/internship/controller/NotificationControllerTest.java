package com.medical.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.NotificationResponse;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 通知控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
public class NotificationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private NotificationService notificationService;
    
    private UserResponse testUser;
    
    @BeforeEach
    public void setUp() {
        testUser = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("STUDENT")
                .status("APPROVED")
                .organization(OrganizationResponse.builder()
                        .id(1L)
                        .name("测试学校")
                        .type("SCHOOL")
                        .code("TEST_SCHOOL_001")
                        .build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        SessionContext.setCurrentUser(testUser);
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    @Test
    public void testGetNotificationListSuccess() throws Exception {
        // 准备测试数据
        NotificationResponse notification1 = NotificationResponse.builder()
                .id(1L)
                .type("APPLICATION_RESULT")
                .content("您的岗位申请已通过审批。")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        NotificationResponse notification2 = NotificationResponse.builder()
                .id(2L)
                .type("WEEKLY_REJECTED")
                .content("您的周记已被打回。")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        List<NotificationResponse> notifications = Arrays.asList(notification1, notification2);
        
        // 模拟service行为
        when(notificationService.getNotificationList()).thenReturn(notifications);
        
        // 执行测试
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].type").value("APPLICATION_RESULT"))
                .andExpect(jsonPath("$.data[1].type").value("WEEKLY_REJECTED"));
        
        // 验证方法调用
        verify(notificationService, times(1)).getNotificationList();
    }
    
    @Test
    public void testMarkAsReadSuccess() throws Exception {
        // 准备测试数据
        NotificationResponse notification = NotificationResponse.builder()
                .id(1L)
                .type("APPLICATION_RESULT")
                .content("您的岗位申请已通过审批。")
                .isRead(true)
                .createdAt(LocalDateTime.now())
                .build();
        
        // 模拟service行为
        when(notificationService.markAsRead(1L)).thenReturn(notification);
        
        // 执行测试
        mockMvc.perform(put("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("标记成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.isRead").value(true));
        
        // 验证方法调用
        verify(notificationService, times(1)).markAsRead(1L);
    }
}
