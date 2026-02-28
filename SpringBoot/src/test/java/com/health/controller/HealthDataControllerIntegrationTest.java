package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.RegisterRequest;
import com.health.entity.HealthData;
import com.health.entity.User;
import com.health.repository.HealthDataRepository;
import com.health.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 健康数据控制器集成测试
 * 测试健康数据的提交、查询、趋势分析等API端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class HealthDataControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthDataRepository healthDataRepository;

    private User testUser;
    private Long userId;

    @BeforeEach
    public void setUp() {
        // 清空数据库
        healthDataRepository.deleteAll();
        userRepository.deleteAll();

        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setName("测试用户");
        testUser.setAge(25);
        testUser.setGender("男");
        testUser.setPhone("13800138000");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        testUser = userRepository.save(testUser);
        userId = testUser.getId();
    }

    @Test
    public void testSubmitHealthDataSuccess() throws Exception {
        // 准备健康数据
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

        // 执行提交健康数据请求
        mockMvc.perform(post("/api/health-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(healthData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("健康数据提交成功"))
                .andExpect(jsonPath("$.data.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.data.height").value(175.50))
                .andExpect(jsonPath("$.data.weight").value(70.00));

        // 验证数据已保存到数据库
        assert healthDataRepository.findByUserId(userId).size() > 0;
    }

    @Test
    public void testSubmitHealthDataMissingUserId() throws Exception {
        // 准备健康数据（缺少userId）
        HealthData healthData = new HealthData();
        healthData.setHeight(new BigDecimal("175.50"));
        healthData.setWeight(new BigDecimal("70.00"));

        // 执行提交健康数据请求
        mockMvc.perform(post("/api/health-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(healthData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户ID不能为空"));
    }

    @Test
    public void testGetUserHealthDataSuccess() throws Exception {
        // 保存多条健康数据
        for (int i = 0; i < 3; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.50").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setBloodPressure("120/80");
            healthData.setHeartRate(72 + i);
            healthData.setRecordedAt(LocalDateTime.now().minusDays(i));
            healthDataRepository.save(healthData);
        }

        // 执行获取用户健康数据请求
        mockMvc.perform(get("/api/health-data")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取健康数据成功"))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    public void testGetUserHealthDataMissingUserId() throws Exception {
        // 执行获取用户健康数据请求（缺少userId参数）
        mockMvc.perform(get("/api/health-data")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户ID不能为空"));
    }

    @Test
    public void testGetHealthTrendsSuccess() throws Exception {
        // 保存多条健康数据
        for (int i = 0; i < 5; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.00").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setBloodPressure("120/80");
            healthData.setHeartRate(72 + i);
            healthData.setRecordedAt(LocalDateTime.now().minusDays(i));
            healthDataRepository.save(healthData);
        }

        // 执行获取健康趋势请求
        mockMvc.perform(get("/api/health-data/trends")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取健康趋势成功"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetHealthDataByRangeSuccess() throws Exception {
        // 保存多条健康数据
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.00").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setRecordedAt(now.minusDays(i));
            healthDataRepository.save(healthData);
        }

        // 准备时间范围
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTime = now.minusDays(3).format(formatter);
        String endTime = now.format(formatter);

        // 执行按时间范围查询请求
        mockMvc.perform(get("/api/health-data/range")
                .param("userId", userId.toString())
                .param("startTime", startTime)
                .param("endTime", endTime)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("按时间范围查询成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetHealthDataByRangeMissingStartTime() throws Exception {
        // 执行按时间范围查询请求（缺少开始时间）
        mockMvc.perform(get("/api/health-data/range")
                .param("userId", userId.toString())
                .param("endTime", "2024-01-01 12:00:00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("开始时间不能为空"));
    }

    @Test
    public void testGetHealthDataByRangeMissingEndTime() throws Exception {
        // 执行按时间范围查询请求（缺少结束时间）
        mockMvc.perform(get("/api/health-data/range")
                .param("userId", userId.toString())
                .param("startTime", "2024-01-01 12:00:00")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("结束时间不能为空"));
    }
}
