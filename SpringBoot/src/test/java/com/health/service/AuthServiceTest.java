package com.health.service;

import com.health.dto.DoctorRegisterRequest;
import com.health.dto.LoginRequest;
import com.health.dto.LoginResponse;
import com.health.dto.RegisterRequest;
import com.health.entity.Doctor;
import com.health.entity.User;
import com.health.repository.DoctorRepository;
import com.health.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 认证服务单元测试
 * 测试用户、医师和管理员的注册和登录逻辑
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("测试用户");
        registerRequest.setAge(25);
        registerRequest.setGender("男");
        registerRequest.setPhone("13800138000");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setName("测试用户");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
    }

    @Test
    public void testRegisterUserSuccess() {
        // 模拟用户名不存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // 执行注册
        User result = authService.registerUser(registerRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("USER", result.getRole());
        assertEquals("ACTIVE", result.getStatus());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUserDuplicateUsername() {
        // 模拟用户名已存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginUserSuccess() {
        // 模拟用户存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 执行登录
        LoginResponse response = authService.loginUser(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals("登录成功", response.getMessage());
    }

    @Test
    public void testLoginUserNotFound() {
        // 模拟用户不存在
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    public void testLoginUserWrongPassword() {
        // 模拟用户存在但密码错误
        loginRequest.setPassword("wrongpassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    public void testLoginUserInactiveAccount() {
        // 模拟用户账户被禁用
        testUser.setStatus("INACTIVE");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(loginRequest));
    }

    @Test
    public void testRegisterDoctorSuccess() {
        // 准备医师注册请求
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setPassword("password123");
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setName("医师张三");
        doctorRequest.setLicenseNumber("DOC123456789");
        doctorRequest.setSpecialization("内科");
        doctorRequest.setHospital("医院A");

        // 模拟用户名和执业证号不存在
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.empty());
        when(doctorRepository.findByLicenseNumber("DOC123456789")).thenReturn(Optional.empty());

        // 模拟保存用户
        User doctorUser = new User();
        doctorUser.setId(2L);
        doctorUser.setUsername("doctor1");
        doctorUser.setRole("DOCTOR");
        when(userRepository.save(any(User.class))).thenReturn(doctorUser);

        // 模拟保存医师
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setUserId(2L);
        doctor.setLicenseNumber("DOC123456789");
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        // 执行注册
        Doctor result = authService.registerDoctor(doctorRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(2L, result.getUserId());
        assertEquals("DOC123456789", result.getLicenseNumber());
        verify(userRepository, times(1)).save(any(User.class));
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    public void testRegisterDoctorDuplicateUsername() {
        // 准备医师注册请求
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setLicenseNumber("DOC123456789");

        // 模拟用户名已存在
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.registerDoctor(doctorRequest));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    public void testRegisterDoctorDuplicateLicense() {
        // 准备医师注册请求
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setLicenseNumber("DOC123456789");

        // 模拟用户名不存在但执业证号已存在
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.empty());
        when(doctorRepository.findByLicenseNumber("DOC123456789")).thenReturn(Optional.of(new Doctor()));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.registerDoctor(doctorRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testLoginDoctorSuccess() {
        // 准备医师用户
        User doctorUser = new User();
        doctorUser.setId(2L);
        doctorUser.setUsername("doctor1");
        doctorUser.setPassword("password123");
        doctorUser.setRole("DOCTOR");
        doctorUser.setStatus("ACTIVE");

        loginRequest.setUsername("doctor1");
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(doctorUser));

        // 执行登录
        LoginResponse response = authService.loginDoctor(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals(2L, response.getUserId());
        assertEquals("DOCTOR", response.getRole());
    }

    @Test
    public void testLoginDoctorNotDoctorRole() {
        // 模拟用户存在但角色不是医师
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.loginDoctor(loginRequest));
    }

    @Test
    public void testLoginAdminSuccess() {
        // 准备管理员用户
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("admin");
        adminUser.setPassword("password123");
        adminUser.setRole("ADMIN");
        adminUser.setStatus("ACTIVE");

        loginRequest.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // 执行登录
        LoginResponse response = authService.loginAdmin(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals(3L, response.getUserId());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    public void testLoginAdminNotAdminRole() {
        // 模拟用户存在但角色不是管理员
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 验证抛出异常
        assertThrows(IllegalArgumentException.class, () -> authService.loginAdmin(loginRequest));
    }
}
