package com.health.service;

import com.health.dto.UserUpdateRequest;
import com.health.entity.User;
import com.health.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 * 测试用户个人信息的获取、更新和查询逻辑
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setName("测试用户");
        testUser.setAge(25);
        testUser.setGender("男");
        testUser.setPhone("13800138000");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
    }

    @Test
    public void testGetUserInfoSuccess() {
        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 执行获取用户信息
        User result = userService.getUserInfo(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getName());
    }

    @Test
    public void testGetUserInfoNotFound() {
        // 模拟用户不存在
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.getUserInfo(1L));
    }

    @Test
    public void testUpdateUserInfoSuccess() {
        // 准备更新请求
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setName("新名字");
        updateRequest.setAge(30);
        updateRequest.setGender("女");
        updateRequest.setPhone("15900000000");

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行更新
        User result = userService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserInfoNotFound() {
        // 模拟用户不存在
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("newemail@example.com");

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserInfo(1L, updateRequest));
    }

    @Test
    public void testUpdateUserInfoInvalidEmail() {
        // 准备无效的邮箱
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("invalid-email");

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserInfo(1L, updateRequest));
    }

    @Test
    public void testUpdateUserInfoInvalidAge() {
        // 准备无效的年龄
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setAge(151); // 超出范围

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserInfo(1L, updateRequest));
    }

    @Test
    public void testUpdateUserInfoInvalidGender() {
        // 准备无效的性别
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setGender("未知");

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserInfo(1L, updateRequest));
    }

    @Test
    public void testUpdateUserInfoInvalidPhone() {
        // 准备无效的电话
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setPhone("123"); // 太短

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserInfo(1L, updateRequest));
    }

    @Test
    public void testGetUserByUsernameSuccess() {
        // 模拟用户存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 执行查询
        User result = userService.getUserByUsername("testuser");

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testGetUserByUsernameNotFound() {
        // 模拟用户不存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.getUserByUsername("testuser"));
    }

    @Test
    public void testGetAllActiveUsers() {
        // 准备活跃用户列表
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setStatus("ACTIVE");

        List<User> activeUsers = Arrays.asList(testUser, user2);

        // 模拟查询
        when(userRepository.findByStatus("ACTIVE")).thenReturn(activeUsers);

        // 执行查询
        List<User> result = userService.getAllActiveUsers();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    public void testGetUsersByRole() {
        // 准备用户列表
        List<User> userList = Arrays.asList(testUser);

        // 模拟查询
        when(userRepository.findByRole("USER")).thenReturn(userList);

        // 执行查询
        List<User> result = userService.getUsersByRole("USER");

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getRole());
        verify(userRepository, times(1)).findByRole("USER");
    }

    @Test
    public void testUpdateUserInfoValidName() {
        // 准备有效的名字
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("张三");

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行更新
        User result = userService.updateUserInfo(1L, updateRequest);

        // 验证结果
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserInfoInvalidName() {
        // 准备无效的名字（包含特殊字符）
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("test@123");

        // 模拟用户存在
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserInfo(1L, updateRequest));
    }
}
