package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        healthDataRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setName("test");
        testUser.setAge(25);
        testUser.setGender("MALE");
        testUser.setPhone("13800138000");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        testUser = userRepository.save(testUser);
        userId = testUser.getId();
    }

    @Test
    public void testSubmitHealthDataSuccess() throws Exception {
        HealthData healthData = new HealthData();
        healthData.setUserId(userId);
        healthData.setHeight(new BigDecimal("175.50"));
        healthData.setWeight(new BigDecimal("70.00"));
        healthData.setBloodPressure("120/80");
        healthData.setHeartRate(72);
        healthData.setBodyTemperature(new BigDecimal("36.60"));
        healthData.setBloodOxygen(98);
        healthData.setBloodSugar(new BigDecimal("5.30"));
        healthData.setSleepDuration(new BigDecimal("7.50"));
        healthData.setDietRecord("diet");
        healthData.setExerciseRecord("run");
        healthData.setDataType("ROUTINE");
        healthData.setRecordedAt(LocalDateTime.now());

        mockMvc.perform(post("/api/health-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.data.height").value(175.50))
                .andExpect(jsonPath("$.data.weight").value(70.00))
                .andExpect(jsonPath("$.data.bodyTemperature").value(36.60))
                .andExpect(jsonPath("$.data.bloodOxygen").value(98))
                .andExpect(jsonPath("$.data.bloodSugar").value(5.30))
                .andExpect(jsonPath("$.data.sleepDuration").value(7.50));
    }

    @Test
    public void testSubmitHealthDataMissingUserId() throws Exception {
        HealthData healthData = new HealthData();
        healthData.setHeight(new BigDecimal("175.50"));
        healthData.setWeight(new BigDecimal("70.00"));

        mockMvc.perform(post("/api/health-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(healthData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    public void testGetUserHealthDataSuccess() throws Exception {
        for (int i = 0; i < 3; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.50").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setBodyTemperature(new BigDecimal("36.50"));
            healthData.setBloodOxygen(98);
            healthData.setBloodSugar(new BigDecimal("5.20"));
            healthData.setSleepDuration(new BigDecimal("7.00"));
            healthData.setBloodPressure("120/80");
            healthData.setHeartRate(72 + i);
            healthData.setRecordedAt(LocalDateTime.now().minusDays(i));
            healthDataRepository.save(healthData);
        }

        mockMvc.perform(get("/api/health-data")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    public void testGetUserHealthDataMissingUserId() throws Exception {
        mockMvc.perform(get("/api/health-data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetHealthTrendsSuccess() throws Exception {
        for (int i = 0; i < 5; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.00").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setBodyTemperature(new BigDecimal("36.50").add(new BigDecimal("0.1")));
            healthData.setBloodOxygen(98);
            healthData.setBloodSugar(new BigDecimal("5.20"));
            healthData.setSleepDuration(new BigDecimal("7.00"));
            healthData.setBloodPressure("120/80");
            healthData.setHeartRate(72 + i);
            healthData.setRecordedAt(LocalDateTime.now().minusDays(i));
            healthDataRepository.save(healthData);
        }

        mockMvc.perform(get("/api/health-data/trends")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetHealthDataByRangeSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setHeight(new BigDecimal("175.00").add(new BigDecimal(i)));
            healthData.setWeight(new BigDecimal("70.00").add(new BigDecimal(i)));
            healthData.setBodyTemperature(new BigDecimal("36.50"));
            healthData.setBloodOxygen(97);
            healthData.setBloodSugar(new BigDecimal("5.10"));
            healthData.setSleepDuration(new BigDecimal("7.00"));
            healthData.setRecordedAt(now.minusDays(i));
            healthDataRepository.save(healthData);
        }

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
    }

    @Test
    public void testGetHealthDataByRangeMissingStartTime() throws Exception {
        mockMvc.perform(get("/api/health-data/range")
                        .param("userId", userId.toString())
                        .param("endTime", "2024-01-01 12:00:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetHealthDataByRangeMissingEndTime() throws Exception {
        mockMvc.perform(get("/api/health-data/range")
                        .param("userId", userId.toString())
                        .param("startTime", "2024-01-01 12:00:00")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
