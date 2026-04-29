package com.medical.internship.service;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.dto.UserRegisterRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.dto.UserUpdateRequest;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.User;
import com.medical.internship.repository.OrganizationRepository;
import com.medical.internship.repository.UserRepository;
import com.medical.internship.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private OrganizationRepository organizationRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private Organization testOrganization;
    private User testUser;
    
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
    }
    
    @Test
    public void testRegisterSuccess() {
        // 准备测试数据
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("newuser")
                .password("password123")
                .email("newuser@example.com")
                .role("STUDENT")
                .organizationId(1L)
                .build();
        
        // 创建新用户（状态为PENDING）
        User newUser = User.builder()
                .id(1L)
                .username("newuser")
                .password("password123")
                .email("newuser@example.com")
                .role("STUDENT")
                .organization(testOrganization)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(testOrganization));
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // 执行测试
        UserResponse response = userService.register(request);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals("STUDENT", response.getRole());
        assertEquals("PENDING", response.getStatus());
        
        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).findByEmail("newuser@example.com");
        verify(organizationRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testRegisterUsernameDuplicate() {
        // 准备测试数据
        UserRegisterRequest request = UserRegisterRequest.builder()
                .username("testuser")
                .password("password123")
                .email("newuser@example.com")
                .role("STUDENT")
                .organizationId(1L)
                .build();
        
        // 模拟repository行为 - 用户名已存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> userService.register(request));
        
        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    public void testLoginSuccess() {
        // 模拟repository行为
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // 执行测试
        UserResponse response = userService.login("testuser", "password123");
        
        // 验证结果
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("APPROVED", response.getStatus());
        
        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    public void testLoginInvalidPassword() {
        // 模拟repository行为
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> userService.login("testuser", "wrongpassword"));
        
        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("testuser");
    }
    
    @Test
    public void testLoginRejectedUser() {
        // 准备被驳回的用户
        User rejectedUser = User.builder()
                .id(2L)
                .username("rejecteduser")
                .password("password123")
                .email("rejected@example.com")
                .role("STUDENT")
                .organization(testOrganization)
                .status("REJECTED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 模拟repository行为
        when(userRepository.findByUsername("rejecteduser")).thenReturn(Optional.of(rejectedUser));
        
        // 执行测试并验证异常
        assertThrows(BusinessException.class, () -> userService.login("rejecteduser", "password123"));
        
        // 验证方法调用
        verify(userRepository, times(1)).findByUsername("rejecteduser");
    }
    
    @Test
    public void testGetUserProfile() {
        // 模拟repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // 执行测试
        UserResponse response = userService.getUserProfile(1L);
        
        // 验证结果
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        
        // 验证方法调用
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testUpdateUserProfile() {
        // 准备测试数据
        UserUpdateRequest request = UserUpdateRequest.builder()
                .email("newemail@example.com")
                .build();
        
        // 模拟repository行为
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // 执行测试
        UserResponse response = userService.updateUserProfile(1L, request);
        
        // 验证结果
        assertNotNull(response);
        
        // 验证方法调用
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByEmail("newemail@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testGetUserByIdNotFound() {
        // 模拟repository行为 - 用户不存在
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
        
        // 验证方法调用
        verify(userRepository, times(1)).findById(999L);
    }
}
