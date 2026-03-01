package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.DoctorUpdateRequest;
import com.health.entity.AuditLog;
import com.health.entity.Doctor;
import com.health.entity.User;
import com.health.repository.AuditLogRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 管理员控制器集成测试
 * 测试用户权限管理、医师管理、数据统计、审计日志等API端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private User adminUser;
    private User normalUser;
    private User doctorUser;
    private Doctor doctor;
    private Long normalUserId;
    private Long doctorId;

    @BeforeEach
    public void setUp() {
        auditLogRepository.deleteAll();
        doctorRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setName("管理员");
        adminUser.setRole("ADMIN");
        adminUser.setStatus("ACTIVE");
        userRepository.save(adminUser);

        normalUser = new User();
        normalUser.setUsername("user1");
        normalUser.setPassword("password123");
        normalUser.setEmail("user1@example.com");
        normalUser.setName("普通用户");
        normalUser.setRole("USER");
        normalUser.setStatus("ACTIVE");
        normalUser = userRepository.save(normalUser);
        normalUserId = normalUser.getId();

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
    public void testGetAllUsersSuccess() throws Exception {
        mockMvc.perform(get("/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取用户列表成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    public void testUpdateUserRoleSuccess() throws Exception {
        mockMvc.perform(put("/admin/users/{id}/role", normalUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"DOCTOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改用户权限成功"))
                .andExpect(jsonPath("$.data.role").value("DOCTOR"));

        User updatedUser = userRepository.findById(normalUserId).orElse(null);
        assert updatedUser != null;
        assert updatedUser.getRole().equals("DOCTOR");
    }

    @Test
    public void testUpdateUserRoleMissingRole() throws Exception {
        mockMvc.perform(put("/admin/users/{id}/role", normalUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("角色不能为空"));
    }

    @Test
    public void testDisableUserAccountSuccess() throws Exception {
        mockMvc.perform(put("/admin/users/{id}/status", normalUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改用户状态成功"))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));

        User updatedUser = userRepository.findById(normalUserId).orElse(null);
        assert updatedUser != null;
        assert updatedUser.getStatus().equals("INACTIVE");
    }

    @Test
    public void testEnableUserAccountSuccess() throws Exception {
        User inactiveUser = new User();
        inactiveUser.setUsername("inactive");
        inactiveUser.setPassword("password123");
        inactiveUser.setRole("USER");
        inactiveUser.setStatus("INACTIVE");
        inactiveUser = userRepository.save(inactiveUser);
        Long inactiveUserId = inactiveUser.getId();

        mockMvc.perform(put("/admin/users/{id}/status", inactiveUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改用户状态成功"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    public void testUpdateUserStatusInvalidStatus() throws Exception {
        mockMvc.perform(put("/admin/users/{id}/status", normalUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("无效的状态值")));
    }

    @Test
    public void testGetAllDoctorsSuccess() throws Exception {
        mockMvc.perform(get("/admin/doctors")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取医师列表成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    public void testUpdateDoctorInfoSuccess() throws Exception {
        DoctorUpdateRequest updateRequest = new DoctorUpdateRequest();
        updateRequest.setSpecialization("外科");
        updateRequest.setHospital("医院B");

        mockMvc.perform(put("/admin/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("编辑医师信息成功"))
                .andExpect(jsonPath("$.data.specialization").value("外科"))
                .andExpect(jsonPath("$.data.hospital").value("医院B"));

        Doctor updatedDoctor = doctorRepository.findById(doctorId).orElse(null);
        assert updatedDoctor != null;
        assert updatedDoctor.getSpecialization().equals("外科");
    }

    @Test
    public void testDeleteDoctorAccountSuccess() throws Exception {
        mockMvc.perform(delete("/admin/doctors/{id}", doctorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除医师账户成功"));

        Doctor deletedDoctor = doctorRepository.findById(doctorId).orElse(null);
        assert deletedDoctor == null;
    }

    @Test
    public void testGetStatisticsSuccess() throws Exception {
        mockMvc.perform(get("/admin/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取数据统计成功"))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetAuditLogsSuccess() throws Exception {
        for (int i = 0; i < 3; i++) {
            AuditLog log = new AuditLog();
            log.setUserId(normalUserId);
            log.setAction("登录");
            log.setResource("用户");
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        }

        mockMvc.perform(get("/admin/audit-logs")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取审计日志成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    public void testGetAuditLogsByUserIdSuccess() throws Exception {
        for (int i = 0; i < 2; i++) {
            AuditLog log = new AuditLog();
            log.setUserId(normalUserId);
            log.setAction("操作" + i);
            log.setResource("资源");
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        }

        mockMvc.perform(get("/admin/audit-logs")
                .param("userId", normalUserId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取审计日志成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    public void testGetAuditLogsByActionSuccess() throws Exception {
        for (int i = 0; i < 2; i++) {
            AuditLog log = new AuditLog();
            log.setUserId(normalUserId);
            log.setAction("登录");
            log.setResource("用户");
            log.setTimestamp(LocalDateTime.now());
            auditLogRepository.save(log);
        }

        mockMvc.perform(get("/admin/audit-logs")
                .param("action", "登录")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取审计日志成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    public void testGetAuditLogsByTimeRangeSuccess() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 2; i++) {
            AuditLog log = new AuditLog();
            log.setUserId(normalUserId);
            log.setAction("操作");
            log.setResource("资源");
            log.setTimestamp(now.minusHours(i));
            auditLogRepository.save(log);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTime = now.minusHours(3).format(formatter);
        String endTime = now.format(formatter);

        mockMvc.perform(get("/admin/audit-logs")
                .param("startTime", startTime)
                .param("endTime", endTime)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取审计日志成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }
}
