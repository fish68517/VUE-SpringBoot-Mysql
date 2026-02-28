package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.DoctorRegisterRequest;
import com.health.dto.LoginRequest;
import com.health.dto.RegisterRequest;
import com.health.entity.User;
import com.health.repository.DoctorRepository;
import com.health.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 * 测试用户、医师和管理员的注册和登录API端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    public void setUp() {
        // 清空数据库
        doctorRepository.deleteAll();
        userRepository.deleteAll();

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
    }

    @Test
    public void testUserRegistrationSuccess() throws Exception {
        // 执行用户注册请求
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户注册成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        // 验证用户已保存到数据库
        User savedUser = userRepository.findByUsername("testuser").orElse(null);
        assert savedUser != null;
        assert savedUser.getEmail().equals("test@example.com");
    }

    @Test
    public void testUserRegistrationDuplicateUsername() throws Exception {
        // 先注册一个用户
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 尝试用相同用户名再次注册
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("用户名已存在")));
    }

    @Test
    public void testUserLoginSuccess() throws Exception {
        // 先注册用户
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 执行登录请求
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    public void testUserLoginInvalidCredentials() throws Exception {
        // 先注册用户
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 尝试用错误密码登录
        loginRequest.setPassword("wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("密码错误")));
    }

    @Test
    public void testUserLoginNonexistentUser() throws Exception {
        // 尝试登录不存在的用户
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("用户不存在")));
    }

    @Test
    public void testDoctorRegistrationSuccess() throws Exception {
        // 准备医师注册请求
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setPassword("password123");
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setName("医师张三");
        doctorRequest.setLicenseNumber("DOC123456789");
        doctorRequest.setSpecialization("内科");
        doctorRequest.setHospital("医院A");

        // 执行医师注册请求
        mockMvc.perform(post("/api/auth/doctor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("医师注册成功"))
                .andExpect(jsonPath("$.data.licenseNumber").value("DOC123456789"));

        // 验证医师已保存到数据库
        User doctorUser = userRepository.findByUsername("doctor1").orElse(null);
        assert doctorUser != null;
        assert doctorUser.getRole().equals("DOCTOR");
    }

    @Test
    public void testDoctorRegistrationDuplicateLicense() throws Exception {
        // 准备医师注册请求
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setPassword("password123");
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setName("医师张三");
        doctorRequest.setLicenseNumber("DOC123456789");
        doctorRequest.setSpecialization("内科");
        doctorRequest.setHospital("医院A");

        // 先注册一个医师
        mockMvc.perform(post("/api/auth/doctor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isOk());

        // 尝试用相同执业证号再次注册
        doctorRequest.setUsername("doctor2");
        mockMvc.perform(post("/api/auth/doctor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("执业证号已存在")));
    }

    @Test
    public void testDoctorLoginSuccess() throws Exception {
        // 先注册医师
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setPassword("password123");
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setName("医师张三");
        doctorRequest.setLicenseNumber("DOC123456789");
        doctorRequest.setSpecialization("内科");
        doctorRequest.setHospital("医院A");

        mockMvc.perform(post("/api/auth/doctor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isOk());

        // 执行医师登录请求
        LoginRequest doctorLogin = new LoginRequest();
        doctorLogin.setUsername("doctor1");
        doctorLogin.setPassword("password123");

        mockMvc.perform(post("/api/auth/doctor/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.role").value("DOCTOR"));
    }

    @Test
    public void testAdminLoginSuccess() throws Exception {
        // 创建管理员用户
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setName("管理员");
        adminUser.setRole("ADMIN");
        adminUser.setStatus("ACTIVE");
        userRepository.save(adminUser);

        // 执行管理员登录请求
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername("admin");
        adminLogin.setPassword("admin123");

        mockMvc.perform(post("/api/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        // 执行登出请求
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }
}
