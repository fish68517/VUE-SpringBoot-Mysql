package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.entity.Consultation;
import com.health.entity.Doctor;
import com.health.entity.HealthAdvice;
import com.health.entity.User;
import com.health.repository.*;
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
 * 医师控制器集成测试
 * 测试医师患者列表、患者档案查看和权限控制
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private HealthAdviceRepository healthAdviceRepository;

    private User doctorUser;
    private User patientUser;
    private Doctor doctor;
    private Long doctorId;
    private Long patientId;

    @BeforeEach
    public void setUp() {
        // 清空数据库
        healthAdviceRepository.deleteAll();
        consultationRepository.deleteAll();
        doctorRepository.deleteAll();
        userRepository.deleteAll();

        // 创建医师用户
        doctorUser = new User();
        doctorUser.setUsername("doctor1");
        doctorUser.setPassword("password123");
        doctorUser.setEmail("doctor@example.com");
        doctorUser.setName("医师张三");
        doctorUser.setRole("DOCTOR");
        doctorUser.setStatus("ACTIVE");
        doctorUser = userRepository.save(doctorUser);

        // 创建医师记录
        doctor = new Doctor();
        doctor.setUserId(doctorUser.getId());
        doctor.setLicenseNumber("DOC123456789");
        doctor.setSpecialization("内科");
        doctor.setHospital("医院A");
        doctor.setVerified(true);
        doctor = doctorRepository.save(doctor);
        doctorId = doctor.getId();

        // 创建患者用户
        patientUser = new User();
        patientUser.setUsername("patient1");
        patientUser.setPassword("password123");
        patientUser.setEmail("patient@example.com");
        patientUser.setName("患者李四");
        patientUser.setRole("USER");
        patientUser.setStatus("ACTIVE");
        patientUser = userRepository.save(patientUser);
        patientId = patientUser.getId();
    }

    @Test
    public void testGetDoctorPatientsSuccess() throws Exception {
        // 创建咨询记录（建立医师与患者的关系）
        Consultation consultation = new Consultation();
        consultation.setUserId(patientId);
        consultation.setDoctorId(doctorId);
        consultation.setQuestion("我最近感觉不舒服");
        consultation.setStatus("PENDING");
        consultationRepository.save(consultation);

        // 执行获取患者列表请求
        mockMvc.perform(get("/api/doctors/patients")
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取患者列表成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testGetDoctorPatientsEmpty() throws Exception {
        // 执行获取患者列表请求（医师没有患者）
        mockMvc.perform(get("/api/doctors/patients")
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取患者列表成功"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void testGetPatientRecordSuccess() throws Exception {
        // 创建咨询记录
        Consultation consultation = new Consultation();
        consultation.setUserId(patientId);
        consultation.setDoctorId(doctorId);
        consultation.setQuestion("我最近感觉不舒服");
        consultation.setStatus("PENDING");
        consultationRepository.save(consultation);

        // 执行获取患者档案请求
        mockMvc.perform(get("/api/doctors/patients/{patientId}", patientId)
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取患者档案成功"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetPatientRecordUnauthorized() throws Exception {
        // 创建另一个医师
        User anotherDoctorUser = new User();
        anotherDoctorUser.setUsername("doctor2");
        anotherDoctorUser.setPassword("password123");
        anotherDoctorUser.setRole("DOCTOR");
        anotherDoctorUser.setStatus("ACTIVE");
        anotherDoctorUser = userRepository.save(anotherDoctorUser);

        Doctor anotherDoctor = new Doctor();
        anotherDoctor.setUserId(anotherDoctorUser.getId());
        anotherDoctor.setLicenseNumber("DOC987654321");
        anotherDoctor = doctorRepository.save(anotherDoctor);

        // 尝试用另一个医师的ID访问患者档案
        mockMvc.perform(get("/api/doctors/patients/{patientId}", patientId)
                .param("doctorId", anotherDoctor.getId().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("无权访问该患者档案")));
    }

    @Test
    public void testGetAllDoctorsAsAdminSuccess() throws Exception {
        // 创建多个医师
        for (int i = 0; i < 2; i++) {
            User doctorUser2 = new User();
            doctorUser2.setUsername("doctor" + (i + 2));
            doctorUser2.setPassword("password123");
            doctorUser2.setRole("DOCTOR");
            doctorUser2.setStatus("ACTIVE");
            doctorUser2 = userRepository.save(doctorUser2);

            Doctor doctor2 = new Doctor();
            doctor2.setUserId(doctorUser2.getId());
            doctor2.setLicenseNumber("DOC" + (i + 2) + "00000000");
            doctorRepository.save(doctor2);
        }

        // 执行获取医师列表请求（管理员）
        mockMvc.perform(get("/api/doctors")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取医师列表成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    public void testGetAllDoctorsAsNonAdminForbidden() throws Exception {
        // 执行获取医师列表请求（非管理员）
        mockMvc.perform(get("/api/doctors")
                .param("role", "USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("权限不足，只有管理员可以访问医师列表"));
    }

    @Test
    public void testGetAllDoctorsWithoutRoleForbidden() throws Exception {
        // 执行获取医师列表请求（不提供role参数）
        mockMvc.perform(get("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("权限不足，只有管理员可以访问医师列表"));
    }
}
