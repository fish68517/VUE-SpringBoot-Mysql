package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.ConsultationAnswerRequest;
import com.health.dto.ConsultationRequest;
import com.health.entity.Consultation;
import com.health.entity.Doctor;
import com.health.entity.User;
import com.health.repository.ConsultationRepository;
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
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 咨询控制器集成测试
 * 测试用户提交咨询、医师回复咨询、查询咨询历史等API端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ConsultationControllerIntegrationTest {

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

    private User patientUser;
    private User doctorUser;
    private Doctor doctor;
    private Long patientId;
    private Long doctorId;

    @BeforeEach
    public void setUp() {
        consultationRepository.deleteAll();
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
    public void testSubmitConsultationSuccess() throws Exception {
        ConsultationRequest consultationRequest = new ConsultationRequest();
        consultationRequest.setQuestion("我最近感觉头晕，请问是什么原因？");

        mockMvc.perform(post("/consultations")
                .param("userId", patientId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("咨询提交成功"))
                .andExpect(jsonPath("$.data.question").value("我最近感觉头晕，请问是什么原因？"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        assert consultationRepository.findByUserId(patientId).size() > 0;
    }

    @Test
    public void testSubmitConsultationEmptyQuestion() throws Exception {
        ConsultationRequest consultationRequest = new ConsultationRequest();
        consultationRequest.setQuestion("");

        mockMvc.perform(post("/consultations")
                .param("userId", patientId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(consultationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("咨询问题不能为空"));
    }

    @Test
    public void testGetConsultationListSuccess() throws Exception {
        for (int i = 0; i < 3; i++) {
            Consultation consultation = new Consultation();
            consultation.setUserId(patientId);
            consultation.setDoctorId(doctorId);
            consultation.setQuestion("问题" + i);
            consultation.setStatus("PENDING");
            consultationRepository.save(consultation);
        }

        mockMvc.perform(get("/consultations")
                .param("userId", patientId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取咨询列表成功"))
                .andExpect(jsonPath("$.data", hasSize(3)));
    }

    @Test
    public void testGetConsultationListEmpty() throws Exception {
        mockMvc.perform(get("/consultations")
                .param("userId", patientId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取咨询列表成功"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void testReplyConsultationSuccess() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setUserId(patientId);
        consultation.setDoctorId(doctorId);
        consultation.setQuestion("我最近感觉头晕");
        consultation.setStatus("PENDING");
        consultation = consultationRepository.save(consultation);
        Long consultationId = consultation.getId();

        ConsultationAnswerRequest answerRequest = new ConsultationAnswerRequest();
        answerRequest.setAnswer("建议您去医院检查一下血压和血糖");

        mockMvc.perform(put("/consultations/{id}/answer", consultationId)
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("咨询回复成功"))
                .andExpect(jsonPath("$.data.answer").value("建议您去医院检查一下血压和血糖"))
                .andExpect(jsonPath("$.data.status").value("ANSWERED"));

        Consultation updatedConsultation = consultationRepository.findById(consultationId).orElse(null);
        assert updatedConsultation != null;
        assert updatedConsultation.getStatus().equals("ANSWERED");
    }

    @Test
    public void testReplyConsultationEmptyAnswer() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setUserId(patientId);
        consultation.setDoctorId(doctorId);
        consultation.setQuestion("我最近感觉头晕");
        consultation.setStatus("PENDING");
        consultation = consultationRepository.save(consultation);
        Long consultationId = consultation.getId();

        ConsultationAnswerRequest answerRequest = new ConsultationAnswerRequest();
        answerRequest.setAnswer("");

        mockMvc.perform(put("/consultations/{id}/answer", consultationId)
                .param("doctorId", doctorId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("回复内容不能为空"));
    }

    @Test
    public void testGetConsultationDetailSuccess() throws Exception {
        Consultation consultation = new Consultation();
        consultation.setUserId(patientId);
        consultation.setDoctorId(doctorId);
        consultation.setQuestion("我最近感觉头晕");
        consultation.setAnswer("建议您去医院检查");
        consultation.setStatus("ANSWERED");
        consultation = consultationRepository.save(consultation);
        Long consultationId = consultation.getId();

        mockMvc.perform(get("/consultations/{id}", consultationId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取咨询详情成功"))
                .andExpect(jsonPath("$.data.question").value("我最近感觉头晕"))
                .andExpect(jsonPath("$.data.answer").value("建议您去医院检查"));
    }

    @Test
    public void testGetConsultationDetailNotFound() throws Exception {
        mockMvc.perform(get("/consultations/{id}", 99999)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("咨询不存在")));
    }
}
