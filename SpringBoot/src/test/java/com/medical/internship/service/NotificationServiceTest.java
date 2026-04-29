package com.medical.internship.service;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.NotificationResponse;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.entity.Notification;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.User;
import com.medical.internship.repository.NotificationRepository;
import com.medical.internship.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 通知服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;
    
    private Organization testOrganization;
    private User testUser;
    private Notification testNotification;
    
    @BeforeEach
    public void setUp() {
        testOrganization = Organization.builder()
                .id(1L)
                .name("测试学校")
                .type("SCHOOL")
                .code("TEST_SCHOOL_001")
                .createdAt(LocalDateTime.now())
                .build();
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .role("STUDENT")
                .organization(testOrganization)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testNotification = Notification.builder()
                .id(1L)
                .user(testUser)
                .type("APPLICATION_RESULT")
                .content("您的岗位申请已通过审批。")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // 设置SessionContext
        UserResponse userResponse = UserResponse.builder()
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
        SessionContext.setCurrentUser(userResponse);
    }
    
    @AfterEach
    public void tearDown() {
        SessionContext.clear();
    }
    
    @Test
    public void testGetNotificationListSuccess() {
        // 准备测试数据
        Notification notification2 = Notification.builder()
                .id(2L)
                .user(testUser)
                .type("WEEKLY_REJECTED")
                .content("您的周记已被打回。")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        List<Notification> notifications = Arrays.asList(testNotification, notification2);
        
        // 模拟repository行为
        when(notificationRepository.findByUserId(1L)).thenReturn(notifications);
        
        // 执行测试
        List<NotificationResponse> responses = notificationService.getNotificationList();
        
        // 验证结果
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("APPLICATION_RESULT", responses.get(0).getType());
        assertEquals("WEEKLY_REJECTED", responses.get(1).getType());
        
        // 验证方法调用
        verify(notificationRepository, times(1)).findByUserId(1L);
    }
    
    @Test
    public void testGetNotificationListNotLoggedIn() {
        // 清除SessionContext
        SessionContext.clear();
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> notificationService.getNotificationList());
    }
    
    @Test
    public void testMarkAsReadSuccess() {
        // 模拟repository行为
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // 执行测试
        NotificationResponse response = notificationService.markAsRead(1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        
        // 验证方法调用
        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
    
    @Test
    public void testMarkAsReadNotFound() {
        // 模拟repository行为 - 通知不存在
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(999L));
        
        // 验证方法调用
        verify(notificationRepository, times(1)).findById(999L);
    }
    
    @Test
    public void testMarkAsReadUnauthorized() {
        // 准备其他用户的通知
        User otherUser = User.builder()
                .id(2L)
                .username("otheruser")
                .password("password123")
                .email("other@example.com")
                .role("STUDENT")
                .organization(testOrganization)
                .status("APPROVED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Notification otherNotification = Notification.builder()
                .id(2L)
                .user(otherUser)
                .type("APPLICATION_RESULT")
                .content("其他用户的通知")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(notificationRepository.findById(2L)).thenReturn(Optional.of(otherNotification));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> notificationService.markAsRead(2L));
        
        // 验证方法调用
        verify(notificationRepository, times(1)).findById(2L);
    }
    
    @Test
    public void testCreateNotification() {
        // 模拟repository行为
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // 执行测试
        Notification result = notificationService.createNotification(
                testUser,
                "APPLICATION_RESULT",
                "您的岗位申请已通过审批。"
        );
        
        // 验证结果
        assertNotNull(result);
        assertEquals("APPLICATION_RESULT", result.getType());
        assertEquals("您的岗位申请已通过审批。", result.getContent());
        assertFalse(result.getIsRead());
        
        // 验证方法调用
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
    
    @Test
    public void testCreateApplicationResultNotificationApproved() {
        // 模拟repository行为
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // 执行测试
        notificationService.createApplicationResultNotification(testUser, "APPROVED", "符合条件");
        
        // 验证方法调用
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
    
    @Test
    public void testCreateApplicationResultNotificationRejected() {
        // 模拟repository行为
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // 执行测试
        notificationService.createApplicationResultNotification(testUser, "REJECTED", "不符合条件");
        
        // 验证方法调用
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
    
    @Test
    public void testCreateWeeklyReportRejectedNotification() {
        // 模拟repository行为
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // 执行测试
        notificationService.createWeeklyReportRejectedNotification(testUser, "内容不完整，请重新提交");
        
        // 验证方法调用
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
    
    @Test
    public void testCreateTaskDeadlineNotification() {
        // 模拟repository行为
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        
        // 执行测试
        notificationService.createTaskDeadlineNotification(testUser, "完成病历整理");
        
        // 验证方法调用
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
