package com.postgraduate.controller;

import com.postgraduate.dto.*;
import com.postgraduate.service.SchoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for SchoolController endpoints.
 * Tests school search and detail retrieval functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchoolService schoolService;

    private SchoolDTO school1;
    private SchoolDTO school2;

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
                .name("Nanjing University")
                .city("Nanjing")
                .tier("985")
                .website("https://www.nju.edu.cn")
                .intro("Leading university in Jiangsu")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testSearchSchoolsWithoutFilters() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school1, school2));
        when(schoolService.searchSchools(null, null, null, null, null, 0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("Tsinghua University"))
                .andExpect(jsonPath("$.data.content[1].name").value("Nanjing University"));
    }

    @Test
    void testSearchSchoolsByCity() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school2));
        when(schoolService.searchSchools("Nanjing", null, null, null, null, 0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .param("city", "Nanjing")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Nanjing University"));
    }

    @Test
    void testSearchSchoolsByTier() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school1, school2));
        when(schoolService.searchSchools(null, "985", null, null, null, 0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .param("tier", "985")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void testSearchSchoolsByCityAndTier() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school2));
        when(schoolService.searchSchools("Nanjing", "985", null, null, null, 0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .param("city", "Nanjing")
                .param("tier", "985")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void testSearchSchoolsWithScoreRange() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school1, school2));
        when(schoolService.searchSchools(null, null, 300, 400, null, 0, null))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .param("expectedScoreMin", "300")
                .param("expectedScoreMax", "400")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void testSearchSchoolsWithPagination() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school1), 0, 1, 2);
        when(schoolService.searchSchools(null, null, null, null, null, 0, 1))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .param("page", "0")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.size").value(1));
    }

    @Test
    void testSearchSchoolsWithAllFilters() throws Exception {
        Page<SchoolDTO> page = new PageImpl<>(Arrays.asList(school2));
        when(schoolService.searchSchools("Nanjing", "985", 300, 400, "Computer Science", 0, 20))
                .thenReturn(page);

        mockMvc.perform(get("/api/schools")
                .param("city", "Nanjing")
                .param("tier", "985")
                .param("expectedScoreMin", "300")
                .param("expectedScoreMax", "400")
                .param("major", "Computer Science")
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void testGetSchoolDetailSuccess() throws Exception {
        SchoolDetailDTO detailDTO = SchoolDetailDTO.builder()
                .id(1L)
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Top university in China")
                .majors(Arrays.asList(
                        MajorDTO.builder().id(1L).schoolId(1L).name("Computer Science").direction("AI").build()
                ))
                .examSubjects(Arrays.asList(
                        ExamSubjectDTO.builder().id(1L).schoolId(1L).subjectName("Math").subjectCode("101").build()
                ))
                .requirements(Arrays.asList(
                        SchoolRequirementDTO.builder().id(1L).schoolId(1L).content("Requirement 1").build()
                ))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(schoolService.getSchoolDetail(1L)).thenReturn(detailDTO);

        mockMvc.perform(get("/api/schools/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Tsinghua University"))
                .andExpect(jsonPath("$.data.majors[0].name").value("Computer Science"))
                .andExpect(jsonPath("$.data.examSubjects[0].subjectName").value("Math"))
                .andExpect(jsonPath("$.data.requirements[0].content").value("Requirement 1"));
    }

    @Test
    void testGetSchoolDetailNotFound() throws Exception {
        when(schoolService.getSchoolDetail(999L))
                .thenThrow(new com.postgraduate.exception.ResourceNotFoundException("School not found"));

        mockMvc.perform(get("/api/schools/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetSchoolRequirementsSuccess() throws Exception {
        when(schoolService.getSchoolRequirements(1L))
                .thenReturn(Arrays.asList(
                        SchoolRequirementDTO.builder().id(1L).schoolId(1L).content("Requirement 1").build(),
                        SchoolRequirementDTO.builder().id(2L).schoolId(1L).content("Requirement 2").build()
                ));

        mockMvc.perform(get("/api/schools/1/requirements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].content").value("Requirement 1"))
                .andExpect(jsonPath("$.data[1].content").value("Requirement 2"));
    }

    @Test
    void testGetSchoolRequirementsEmpty() throws Exception {
        when(schoolService.getSchoolRequirements(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/schools/1/requirements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetSchoolRequirementsNotFound() throws Exception {
        when(schoolService.getSchoolRequirements(999L))
                .thenThrow(new com.postgraduate.exception.ResourceNotFoundException("School not found"));

        mockMvc.perform(get("/api/schools/999/requirements")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetSchoolExamSubjectsSuccess() throws Exception {
        when(schoolService.getSchoolExamSubjects(1L))
                .thenReturn(Arrays.asList(
                        ExamSubjectDTO.builder().id(1L).schoolId(1L).subjectName("Math").subjectCode("101").build(),
                        ExamSubjectDTO.builder().id(2L).schoolId(1L).subjectName("English").subjectCode("201").build()
                ));

        mockMvc.perform(get("/api/schools/1/subjects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].subjectName").value("Math"))
                .andExpect(jsonPath("$.data[1].subjectName").value("English"));
    }

    @Test
    void testGetSchoolExamSubjectsEmpty() throws Exception {
        when(schoolService.getSchoolExamSubjects(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/schools/1/subjects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void testGetSchoolExamSubjectsNotFound() throws Exception {
        when(schoolService.getSchoolExamSubjects(999L))
                .thenThrow(new com.postgraduate.exception.ResourceNotFoundException("School not found"));

        mockMvc.perform(get("/api/schools/999/subjects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
