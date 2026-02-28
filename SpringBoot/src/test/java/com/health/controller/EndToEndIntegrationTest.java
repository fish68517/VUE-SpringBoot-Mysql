package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.*;
import com.health.entity.HealthData;
import com.health.repository.*;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 端到端集成测试
 * 测试完整的业务流程：用户注册→登录→提交健康数据→查询数据
 * 以及医师查看患者档案、管理员管理用户等完整流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HealthDataRepository healthDataRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private HealthAdviceRepository healthAdviceRepository;

    @BeforeEach
    public void setUp() {
        // 清空所有数据库表
        healthAdviceRepository.deleteAll();
        consultationRepository.deleteAll();
        healthDataRepository.deleteAll();
        doctorRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCompleteUserWorkflow() throws Exception {
        // 1. 用户注册
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("测试用户");
        registerRequest.setAge(25);
        registerRequest.setGender("男");
        registerRequest.setPhone("13800138000");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        // 从响应中提取用户ID
        String registerResponse = registerResult.getResponse().getContentAsString();
        Long userId = objectMapper.readTree(registerResponse).get("data").get("id").asLong();

        // 2. 用户登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(userId.intValue()));

        // 3. 用户提交健康数据
        HealthData healthData = new HealthData();
        healthData.setUserId(userId);
        healthData.setHeight(new BigDecimal("175.50"));
        healthData.setWeight(new BigDecimal("70.00"));
        healthData.setBloodPressure("120/80");
        healthData.setHeartRate(72);
        healthData.setDietRecord("早餐：粥、鸡蛋");
        healthData.setExerciseRecord("跑步30分钟");
        healthData.setDataType("ROUTINE");
        healthData.setRecordedAt(LocalDateTime.now());

        mockMvc.perform(post("/api/health-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(healthData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("健康数据提交成功"));

        // 4. 用户查询自己的健康数据
        mockMvc.perform(get("/api/health-data")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].height").value(175.5));

        // 5. 用户查询个人信息
        mockMvc.perform(get("/api/users/profile")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        // 6. 用户更新个人信息
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setName("新名字");

        mockMvc.perform(put("/api/users/profile")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.email").value("newemail@example.com"));
    }

    @Test
    public void testCompleteDoctorWorkflow() throws Exception {
        // 1. 医师注册
        DoctorRegisterRequest doctorRequest = new DoctorRegisterRequest();
        doctorRequest.setUsername("doctor1");
        doctorRequest.setPassword("password123");
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setName("医师张三");
        doctorRequest.setLicenseNumber("DOC123456789");
        doctorRequest.setSpecialization("内科");
        doctorRequest.setHospital("医院A");

        MvcResult doctorRegisterResult = mockMvc.perform(post("/api/auth/doctor/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String doctorRegisterResponse = doctorRegisterResult.getResponse().getContentAsString();
        Long doctorId = objectMapper.readTree(doctorRegisterResponse).get("data").get("id").asLong();

        // 2. 医师登录
        LoginRequest doctorLogin = new LoginRequest();
        doctorLogin.setUsername("doctor1");
        doctorLogin.setPassword("password123");

        mockMvc.perform(post("/api/auth/doctor/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(doctorLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.role").value("DOCTOR"));

        // 3. 创建患者用户
        RegisterRequest patientRequest = new RegisterRequest();
        patientRequest.setUsername("patient1");
        patientRequest.setPassword("password123");
        patientRequest.setEmail("patient@example.com");
        patientRequest.setName("患者李四");

        MvcResult patientResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String patientResponse = patientResult.getResponse().getContentAsString();
        Long patientId = objectMapper.readTree(patientResponse).get("data").get("id").asLong();

        // 4. 患者提交健康数据
        HealthData healthData = new HealthData();
        healthData.setUserId(patientId);
        healthData.setHeight(new BigDecimal("165.00"));
        healthData.setWeight(new BigDecimal("60.00"));
        healthData.setBloodPressure("110/70");
        healthData.setHeartRate(68);
        healthData.setRecordedAt(LocalDateTime.now());

        mockMvc.perform(post("/api/health-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(healthData)))
                .andExpect(status().isOk());

        // 5. 医师查询患者列表（初始为空）
        mockMvc.perform(get("/api/doctors/patients")
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    public void testCompleteAdminWorkflow() throws Exception {
        // 1. 创建管理员用户
        com.health.entity.User adminUser = new com.health.entity.User();
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setName("管理员");
        adminUser.setRole("ADMIN");
        adminUser.setStatus("ACTIVE");
        userRepository.save(adminUser);

        // 2. 管理员登录
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setUsername("admin");
        adminLogin.setPassword("admin123");

        mockMvc.perform(post("/api/auth/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));

        // 3. 创建多个用户
        for (int i = 0; i < 3; i++) {
            RegisterRequest userRequest = new RegisterRequest();
            userRequest.setUsername("user" + i);
            userRequest.setPassword("password123");
            userRequest.setEmail("user" + i + "@example.com");
            userRequest.setName("用户" + i);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
                    .andExpect(status().isOk());
        }

        // 4. 管理员查询用户列表
        mockMvc.perform(get("/api/users")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));

        // 5. 创建医师
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

        // 6. 管理员查询医师列表
        mockMvc.perform(get("/api/doctors")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testHealthDataTimeRangeQuery() throws Exception {
        // 1. 创建用户
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("测试用户");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        Long userId = objectMapper.readTree(registerResponse).get("data").get("id").asLong();

        // 2. 提交多条健康数据
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.00").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setRecordedAt(now.minusDays(i));

            mockMvc.perform(post("/api/health-data")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(healthData)))
                    .andExpect(status().isOk());
        }

        // 3. 按时间范围查询
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTime = now.minusDays(3).format(formatter);
        String endTime = now.format(formatter);

        mockMvc.perform(get("/api/health-data/range")
                .param("userId", userId.toString())
                .param("startTime", startTime)
                .param("endTime", endTime)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        // 4. 查询健康趋势
        mockMvc.perform(get("/api/health-data/trends")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}
