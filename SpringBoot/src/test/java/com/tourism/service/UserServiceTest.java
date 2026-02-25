package com.tourism.service;

import com.tourism.entity.User;
import com.tourism.exception.BusinessException;
import com.tourism.exception.ValidationException;
import com.tourism.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    public void testRegisterSuccess() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";
        String phone = "13800138000";
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setEmail(email);
        mockUser.setPhone(phone);
        mockUser.setRole("tourist");
        mockUser.setStatus("active");
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByPhone(phone)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        
        // 执行注册
        User result = userService.register(username, password, email, phone);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("tourist", result.getRole());
        assertEquals("active", result.getStatus());
        
        // 验证save方法被调用
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testRegisterWithDuplicateUsername() {
        // 设置用户名已存在
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.register("testuser", "password123", "test@example.com", "13800138000");
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testRegisterWithDuplicateEmail() {
        // 设置邮箱已存在
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.register("testuser", "password123", "test@example.com", "13800138000");
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testRegisterWithDuplicatePhone() {
        // 设置手机号已存在
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("13800138000")).thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.register("testuser", "password123", "test@example.com", "13800138000");
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testRegisterWithInvalidUsername() {
        // 验证用户名格式不正确时抛出异常
        assertThrows(ValidationException.class, () -> {
            userService.register("ab", "password123", "test@example.com", "13800138000");
        });
    }
    
    @Test
    public void testRegisterWithInvalidPassword() {
        // 验证密码长度不足时抛出异常
        assertThrows(ValidationException.class, () -> {
            userService.register("testuser", "123", "test@example.com", "13800138000");
        });
    }
    
    @Test
    public void testRegisterWithInvalidEmail() {
        // 验证邮箱格式不正确时抛出异常
        assertThrows(ValidationException.class, () -> {
            userService.register("testuser", "password123", "invalid-email", "13800138000");
        });
    }
    
    @Test
    public void testRegisterWithInvalidPhone() {
        // 验证手机号格式不正确时抛出异常
        assertThrows(ValidationException.class, () -> {
            userService.register("testuser", "password123", "test@example.com", "12345678901");
        });
    }
    
    @Test
    public void testLoginSuccess() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setEmail("test@example.com");
        mockUser.setPhone("13800138000");
        mockUser.setRole("tourist");
        mockUser.setStatus("active");
        
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        
        // 执行登录
        User result = userService.login(username, password);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("active", result.getStatus());
        
        // 验证findByUsername方法被调用
        verify(userRepository, times(1)).findByUsername(username);
    }
    
    @Test
    public void testLoginWithNonexistentUser() {
        // 设置用户不存在
        when(userRepository.findByUsername("nonexistent")).thenReturn(java.util.Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.login("nonexistent", "password123");
        });
    }
    
    @Test
    public void testLoginWithWrongPassword() {
        // 准备测试数据
        String username = "testuser";
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword("correctpassword");
        mockUser.setRole("tourist");
        mockUser.setStatus("active");
        
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.login(username, "wrongpassword");
        });
    }
    
    @Test
    public void testLoginWithDisabledUser() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(password);
        mockUser.setRole("tourist");
        mockUser.setStatus("disabled");
        
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.login(username, password);
        });
    }
    
    @Test
    public void testLoginWithInvalidUsername() {
        // 验证用户名格式不正确时抛出异常
        assertThrows(ValidationException.class, () -> {
            userService.login("ab", "password123");
        });
    }
    
    @Test
    public void testLoginWithInvalidPassword() {
        // 验证密码长度不足时抛出异常
        assertThrows(ValidationException.class, () -> {
            userService.login("testuser", "123");
        });
    }
    
    @Test
    public void testGetUserProfileSuccess() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPhone("13800138000");
        mockUser.setRealName("Test User");
        mockUser.setRole("tourist");
        mockUser.setStatus("active");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        
        // 执行获取用户信息
        User result = userService.getUserProfile(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getRealName());
        
        // 验证findById方法被调用
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetUserProfileWithNonexistentUser() {
        // 设置用户不存在
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.getUserProfile(999L);
        });
    }
    
    @Test
    public void testUpdateUserProfileSuccess() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("old@example.com");
        mockUser.setPhone("13800138000");
        mockUser.setRealName("Old Name");
        mockUser.setRole("tourist");
        mockUser.setStatus("active");
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("new@example.com");
        updatedUser.setPhone("13900139000");
        updatedUser.setRealName("New Name");
        updatedUser.setRole("tourist");
        updatedUser.setStatus("active");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("13900139000")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        // 执行更新用户信息
        User result = userService.updateUserProfile(1L, "New Name", "new@example.com", "13900139000");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("New Name", result.getRealName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("13900139000", result.getPhone());
        
        // 验证save方法被调用
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testUpdateUserProfileWithNonexistentUser() {
        // 设置用户不存在
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.updateUserProfile(999L, "New Name", "new@example.com", "13900139000");
        });
    }
    
    @Test
    public void testUpdateUserProfileWithDuplicateEmail() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("old@example.com");
        mockUser.setPhone("13800138000");
        mockUser.setRealName("Old Name");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.updateUserProfile(1L, "New Name", "duplicate@example.com", "13800138000");
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testUpdateUserProfileWithDuplicatePhone() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setPhone("13800138000");
        mockUser.setRealName("Old Name");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.existsByPhone("13900139000")).thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.updateUserProfile(1L, "New Name", "test@example.com", "13900139000");
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testGetUserListSuccess() {
        // 准备测试数据
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setStatus("active");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setStatus("active");
        
        java.util.List<User> userList = java.util.Arrays.asList(user1, user2);
        org.springframework.data.domain.Page<User> mockPage = 
            new org.springframework.data.domain.PageImpl<>(userList, 
                org.springframework.data.domain.PageRequest.of(0, 10), 2);
        
        when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(mockPage);
        
        // 执行获取用户列表
        org.springframework.data.domain.Page<User> result = userService.getUserList(0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        
        // 验证findAll方法被调用
        verify(userRepository, times(1)).findAll(any(org.springframework.data.domain.Pageable.class));
    }
    
    @Test
    public void testDisableUserSuccess() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setStatus("active");
        
        User disabledUser = new User();
        disabledUser.setId(1L);
        disabledUser.setUsername("testuser");
        disabledUser.setStatus("disabled");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(disabledUser);
        
        // 执行禁用用户
        User result = userService.disableUser(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("disabled", result.getStatus());
        
        // 验证save方法被调用
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testDisableUserWithNonexistentUser() {
        // 设置用户不存在
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.disableUser(999L);
        });
    }
    
    @Test
    public void testDisableAlreadyDisabledUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setStatus("disabled");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.disableUser(1L);
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testEnableUserSuccess() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setStatus("disabled");
        
        User enabledUser = new User();
        enabledUser.setId(1L);
        enabledUser.setUsername("testuser");
        enabledUser.setStatus("active");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(enabledUser);
        
        // 执行启用用户
        User result = userService.enableUser(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("active", result.getStatus());
        
        // 验证save方法被调用
        verify(userRepository, times(1)).save(any(User.class));
    }
    
    @Test
    public void testEnableUserWithNonexistentUser() {
        // 设置用户不存在
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.enableUser(999L);
        });
    }
    
    @Test
    public void testEnableAlreadyEnabledUser() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setStatus("active");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.enableUser(1L);
        });
        
        // 验证save方法未被调用
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void testDeleteUserSuccess() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        
        // 执行删除用户
        userService.deleteUser(1L);
        
        // 验证deleteById方法被调用
        verify(userRepository, times(1)).deleteById(1L);
    }
    
    @Test
    public void testDeleteUserWithNonexistentUser() {
        // 设置用户不存在
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            userService.deleteUser(999L);
        });
        
        // 验证deleteById方法未被调用
        verify(userRepository, never()).deleteById(any());
    }
}
