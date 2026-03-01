package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.HealthAdviceRequest;
import com.health.entity.Doctor;
import com.health.entity.HealthAdvice;
import com.health.entity.User;
import com.health.repository.DoctorRepository;
import com.health.repository.HealthAdviceRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 健康建议控制器集成测试
 * 测试医师创建健康建议、患者查看建议等API端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class HealthAdviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private HealthAdviceRepository healthAdviceRepository;

    private User patientUser;
    private User doctorUser;
    private Doctor doctor;
    private Long patientId;
    private Long doctorId;

    @BeforeEach
    public void setUp() {
        healthAdviceRepository.deleteAll();
        doctorRepository.deleteAll();
        userRepository.deleteAll();

        patientUser = new User();
        patientUser.setUsername("patient1");
        patientUser.setPassword("password123");
        patientUser.setEmail("patient@example.com");
        patientUser.setName("患者李四");
        patientUser.setRole("USER");
        patientUser.setStatus("ACTIVE");
        patientUser = userRepository.save(patientUser);
        patientId = patientUser.getId();

        doctorUser = new User();
        doctorUser.setUsername("doctor1");
        doctorUser.setPassword("password123");
        doctorUser.setEmail("doctor@example.com");
        doctorUser.setName("医师张三");
        doctorUser.setRole("DOCTOR");
        doctorUser.setStatus("ACTIVE");
        doctorUser = userRepository.save(doctorUser);

        doctor = new Doctor();
        doctor.setUserId(doctorUser.getId());
        doctor.setLicenseNumber("DOC123456789");
        doctor.setSpecialization("内科");
        doctor.setHospital("医院A");
        doctor.setVerified(true);
        doctor = doctorRepository.save(doctor);
        doctorId = doctor.getId();
    }

    @Test
    public void testCreateHealthAdviceSuccess() throws Exception {
        HealthAdviceRequest adviceRequest = new HealthAdviceRequest();
        adviceRequest.setPatientId(patientId);
        adviceRequest.setAdviceContent("建议您每天坚持运动30分钟，保持健康的生活方式");
        adviceRequest.setRecommendation("每周运动5次，每次30分钟");

        mockMvc.perform(post("/health-advice")
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adviceRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("健康建议创建成功"))
                .andExpect(jsonPath("$.data.adviceContent").value("建议您每天坚持运动30分钟，保持健康的生活方式"));

        assert healthAdviceRepository.findByPatientId(patientId).size() > 0;
    }

    @Test
    public void testCreateHealthAdviceMissingPatientId() throws Exception {
        HealthAdviceRequest adviceRequest = new HealthAdviceRequest();
        adviceRequest.setAdviceContent("建议您每天坚持运动30分钟");

        mockMvc.perform(post("/health-advice")
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adviceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("患者ID不能为空"));
    }

    @Test
    public void testCreateHealthAdviceMissingContent() throws Exception {
        HealthAdviceRequest adviceRequest = new HealthAdviceRequest();
        adviceRequest.setPatientId(patientId);
        adviceRequest.setAdviceContent("");

        mockMvc.perform(post("/health-advice")
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adviceRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("建议内容不能为空"));
    }

    @Test
    public void testGetPatientHealthAdviceSuccess() throws Exception {
        for (int i = 0; i < 3; i++) {
            HealthAdvice advice = new HealthAdvice();
            advice.setDoctorId(doctorId);
            advice.setPatientId(patientId);
            advice.setAdviceContent("建议" + i);
            advice.setRecommendation("推荐" + i);
            healthAdviceRepository.save(advice);
        }

        mockMvc.perform(get("/health-advice/patient/{patientId}", patientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取患者健康建议成功"))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    public void testGetPatientHealthAdviceEmpty() throws Exception {
        mockMvc.perform(get("/health-advice/patient/{patientId}", patientId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取患者健康建议成功"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void testGetMyHealthAdviceSuccess() throws Exception {
        for (int i = 0; i < 2; i++) {
            HealthAdvice advice = new HealthAdvice();
            advice.setDoctorId(doctorId);
            advice.setPatientId(patientId);
            advice.setAdviceContent("我的建议" + i);
            healthAdviceRepository.save(advice);
        }

        mockMvc.perform(get("/health-advice/my-advice")
                .param("userId", patientId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取我的健康建议成功"))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    public void testGetMyHealthAdviceEmpty() throws Exception {
        mockMvc.perform(get("/health-advice/my-advice")
                .param("userId", patientId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取我的健康建议成功"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
