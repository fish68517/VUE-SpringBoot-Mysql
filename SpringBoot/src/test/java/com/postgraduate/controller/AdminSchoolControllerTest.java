package com.postgraduate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgraduate.dto.ExamSubjectCreateRequest;
import com.postgraduate.dto.ExamSubjectDTO;
import com.postgraduate.dto.MajorCreateRequest;
import com.postgraduate.dto.MajorDTO;
import com.postgraduate.dto.SchoolCreateRequest;
import com.postgraduate.dto.SchoolDTO;
import com.postgraduate.dto.SchoolUpdateRequest;
import com.postgraduate.service.AdminSchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AdminSchoolController endpoints.
 * Tests admin school management functionality including creation, update, and deletion.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminSchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminSchoolService adminSchoolService;

    private SchoolDTO school1;
    private SchoolDTO school2;
    private MajorDTO major1;
    private ExamSubjectDTO examSubject1;

    @BeforeEach
    void setUp() {
        school1 = SchoolDTO.builder()
                .id(1L)
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Top university in China")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        school2 = SchoolDTO.builder()
                .id(2L)
                .name("Peking University")
                .city("Beijing")
                .tier("985")
                .website("https://www.pku.edu.cn")
                .intro("Leading research university")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        major1 = MajorDTO.builder()
                .id(1L)
                .schoolId(1L)
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        examSubject1 = ExamSubjectDTO.builder()
                .id(1L)
                .schoolId(1L)
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateSchoolSuccess() throws Exception {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Top university in China")
                .build();

        when(adminSchoolService.createSchool(any(SchoolCreateRequest.class))).thenReturn(school1);

        mockMvc.perform(post("/api/admin/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.name").value("Tsinghua University"))
                .andExpect(jsonPath("$.data.city").value("Beijing"))
                .andExpect(jsonPath("$.data.tier").value("985"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateSchoolWithMissingName() throws Exception {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .city("Beijing")
                .tier("985")
                .build();

        mockMvc.perform(post("/api/admin/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateSchoolWithMissingCity() throws Exception {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .tier("985")
                .build();

        mockMvc.perform(post("/api/admin/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateSchoolWithMissingTier() throws Exception {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .build();

        mockMvc.perform(post("/api/admin/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSchoolWithoutAuthentication() throws Exception {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .build();

        mockMvc.perform(post("/api/admin/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateSchoolWithUserRole() throws Exception {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .build();

        mockMvc.perform(post("/api/admin/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateSchoolSuccess() throws Exception {
        SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                .name("Tsinghua University Updated")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Updated introduction")
                .build();

        SchoolDTO updatedSchool = SchoolDTO.builder()
                .id(1L)
                .name("Tsinghua University Updated")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Updated introduction")
                .build();

        when(adminSchoolService.updateSchool(eq(1L), any(SchoolUpdateRequest.class))).thenReturn(updatedSchool);

        mockMvc.perform(put("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("Tsinghua University Updated"))
                .andExpect(jsonPath("$.data.intro").value("Updated introduction"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateSchoolWithMissingName() throws Exception {
        SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                .city("Beijing")
                .tier("985")
                .build();

        mockMvc.perform(put("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSchoolWithoutAuthentication() throws Exception {
        SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .build();

        mockMvc.perform(put("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUpdateSchoolWithUserRole() throws Exception {
        SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .build();

        mockMvc.perform(put("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testDeleteSchoolSuccess() throws Exception {
        doNothing().when(adminSchoolService).deleteSchool(1L);

        mockMvc.perform(delete("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("School deleted successfully"));
    }

    @Test
    void testDeleteSchoolWithoutAuthentication() throws Exception {
        mockMvc.perform(delete("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testDeleteSchoolWithUserRole() throws Exception {
        mockMvc.perform(delete("/api/admin/schools/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateMajorSuccess() throws Exception {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .build();

        when(adminSchoolService.createMajor(eq(1L), any(MajorCreateRequest.class))).thenReturn(major1);

        mockMvc.perform(post("/api/admin/schools/1/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.name").value("Computer Science"))
                .andExpect(jsonPath("$.data.direction").value("Artificial Intelligence"))
                .andExpect(jsonPath("$.data.schoolId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateMajorWithMissingName() throws Exception {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .direction("Artificial Intelligence")
                .build();

        mockMvc.perform(post("/api/admin/schools/1/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMajorWithoutAuthentication() throws Exception {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .build();

        mockMvc.perform(post("/api/admin/schools/1/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateMajorWithUserRole() throws Exception {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .build();

        mockMvc.perform(post("/api/admin/schools/1/majors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateExamSubjectSuccess() throws Exception {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        when(adminSchoolService.createExamSubject(eq(1L), any(ExamSubjectCreateRequest.class))).thenReturn(examSubject1);

        mockMvc.perform(post("/api/admin/schools/1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.subjectName").value("Data Structures"))
                .andExpect(jsonPath("$.data.subjectCode").value("CS101"))
                .andExpect(jsonPath("$.data.schoolId").value(1))
                .andExpect(jsonPath("$.data.majorId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testCreateExamSubjectWithMissingSubjectName() throws Exception {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectCode("CS101")
                .build();

        mockMvc.perform(post("/api/admin/schools/1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateExamSubjectWithoutAuthentication() throws Exception {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        mockMvc.perform(post("/api/admin/schools/1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testCreateExamSubjectWithUserRole() throws Exception {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        mockMvc.perform(post("/api/admin/schools/1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}
