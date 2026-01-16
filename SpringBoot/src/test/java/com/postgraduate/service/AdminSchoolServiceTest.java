package com.postgraduate.service;

import com.postgraduate.dto.ExamSubjectCreateRequest;
import com.postgraduate.dto.ExamSubjectDTO;
import com.postgraduate.dto.MajorCreateRequest;
import com.postgraduate.dto.MajorDTO;
import com.postgraduate.dto.SchoolCreateRequest;
import com.postgraduate.dto.SchoolDTO;
import com.postgraduate.dto.SchoolUpdateRequest;
import com.postgraduate.entity.ExamSubject;
import com.postgraduate.entity.Major;
import com.postgraduate.entity.School;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.ExamSubjectRepository;
import com.postgraduate.repository.MajorRepository;
import com.postgraduate.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for AdminSchoolService.
 * Tests school CRUD operations including creation, update, and deletion.
 */
@SpringBootTest
class AdminSchoolServiceTest {

    @Autowired
    private AdminSchoolService adminSchoolService;

    @MockBean
    private SchoolRepository schoolRepository;

    @MockBean
    private MajorRepository majorRepository;

    @MockBean
    private ExamSubjectRepository examSubjectRepository;

    private School school;
    private SchoolCreateRequest createRequest;
    private SchoolUpdateRequest updateRequest;
    private Major major;
    private ExamSubject examSubject;

    @BeforeEach
    void setUp() {
        school = School.builder()
                .id(1L)
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Top university in China")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Top university in China")
                .build();

        updateRequest = SchoolUpdateRequest.builder()
                .name("Tsinghua University Updated")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Updated introduction")
                .build();

        major = Major.builder()
                .id(1L)
                .schoolId(1L)
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        examSubject = ExamSubject.builder()
                .id(1L)
                .schoolId(1L)
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateSchoolSuccess() {
        when(schoolRepository.save(any(School.class))).thenReturn(school);

        SchoolDTO result = adminSchoolService.createSchool(createRequest);

        assertNotNull(result);
        assertEquals("Tsinghua University", result.getName());
        assertEquals("Beijing", result.getCity());
        assertEquals("985", result.getTier());
        verify(schoolRepository, times(1)).save(any(School.class));
    }

    @Test
    void testCreateSchoolWithEmptyName() {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("")
                .city("Beijing")
                .tier("985")
                .build();

        assertThrows(ValidationException.class, () -> adminSchoolService.createSchool(request));
    }

    @Test
    void testCreateSchoolWithEmptyCity() {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("")
                .tier("985")
                .build();

        assertThrows(ValidationException.class, () -> adminSchoolService.createSchool(request));
    }

    @Test
    void testCreateSchoolWithEmptyTier() {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("")
                .build();

        assertThrows(ValidationException.class, () -> adminSchoolService.createSchool(request));
    }

    @Test
    void testCreateSchoolWithInvalidTier() {
        SchoolCreateRequest request = SchoolCreateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("INVALID_TIER")
                .build();

        assertThrows(ValidationException.class, () -> adminSchoolService.createSchool(request));
    }

    @Test
    void testCreateSchoolWithValidTiers() {
        String[] validTiers = {"985", "211", "DOUBLE_NON", "OTHER"};

        for (String tier : validTiers) {
            SchoolCreateRequest request = SchoolCreateRequest.builder()
                    .name("Test University")
                    .city("Beijing")
                    .tier(tier)
                    .build();

            School testSchool = School.builder()
                    .id(1L)
                    .name("Test University")
                    .city("Beijing")
                    .tier(tier)
                    .deleted(false)
                    .build();

            when(schoolRepository.save(any(School.class))).thenReturn(testSchool);

            SchoolDTO result = adminSchoolService.createSchool(request);
            assertNotNull(result);
            assertEquals(tier, result.getTier());
        }
    }

    @Test
    void testUpdateSchoolSuccess() {
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(schoolRepository.save(any(School.class))).thenReturn(school);

        SchoolDTO result = adminSchoolService.updateSchool(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Tsinghua University Updated", result.getName());
        verify(schoolRepository, times(1)).findById(1L);
        verify(schoolRepository, times(1)).save(any(School.class));
    }

    @Test
    void testUpdateSchoolNotFound() {
        when(schoolRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSchoolService.updateSchool(999L, updateRequest));
    }

    @Test
    void testUpdateSchoolWithEmptyName() {
        SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                .name("")
                .city("Beijing")
                .tier("985")
                .build();

        assertThrows(ValidationException.class, () -> adminSchoolService.updateSchool(1L, request));
    }

    @Test
    void testUpdateSchoolWithInvalidTier() {
        SchoolUpdateRequest request = SchoolUpdateRequest.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("INVALID_TIER")
                .build();

        assertThrows(ValidationException.class, () -> adminSchoolService.updateSchool(1L, request));
    }

    @Test
    void testDeleteSchoolSuccess() {
        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(schoolRepository.save(any(School.class))).thenReturn(school);

        adminSchoolService.deleteSchool(1L);

        verify(schoolRepository, times(1)).findById(1L);
        verify(schoolRepository, times(1)).save(any(School.class));
    }

    @Test
    void testDeleteSchoolNotFound() {
        when(schoolRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSchoolService.deleteSchool(999L));
    }

    @Test
    void testCreateMajorSuccess() {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(majorRepository.save(any(Major.class))).thenReturn(major);

        MajorDTO result = adminSchoolService.createMajor(1L, request);

        assertNotNull(result);
        assertEquals("Computer Science", result.getName());
        assertEquals("Artificial Intelligence", result.getDirection());
        assertEquals(1L, result.getSchoolId());
        verify(schoolRepository, times(1)).findById(1L);
        verify(majorRepository, times(1)).save(any(Major.class));
    }

    @Test
    void testCreateMajorSchoolNotFound() {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .build();

        when(schoolRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSchoolService.createMajor(999L, request));
    }

    @Test
    void testCreateMajorWithEmptyName() {
        MajorCreateRequest request = MajorCreateRequest.builder()
                .name("")
                .direction("Artificial Intelligence")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

        assertThrows(ValidationException.class, () -> adminSchoolService.createMajor(1L, request));
    }

    @Test
    void testCreateExamSubjectSuccess() {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(examSubjectRepository.save(any(ExamSubject.class))).thenReturn(examSubject);

        ExamSubjectDTO result = adminSchoolService.createExamSubject(1L, request);

        assertNotNull(result);
        assertEquals("Data Structures", result.getSubjectName());
        assertEquals("CS101", result.getSubjectCode());
        assertEquals(1L, result.getSchoolId());
        assertEquals(1L, result.getMajorId());
        verify(schoolRepository, times(1)).findById(1L);
        verify(majorRepository, times(1)).findById(1L);
        verify(examSubjectRepository, times(1)).save(any(ExamSubject.class));
    }

    @Test
    void testCreateExamSubjectWithoutMajor() {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(examSubjectRepository.save(any(ExamSubject.class))).thenReturn(examSubject);

        ExamSubjectDTO result = adminSchoolService.createExamSubject(1L, request);

        assertNotNull(result);
        assertEquals("Data Structures", result.getSubjectName());
        verify(schoolRepository, times(1)).findById(1L);
        verify(examSubjectRepository, times(1)).save(any(ExamSubject.class));
    }

    @Test
    void testCreateExamSubjectSchoolNotFound() {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        when(schoolRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSchoolService.createExamSubject(999L, request));
    }

    @Test
    void testCreateExamSubjectMajorNotFound() {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(999L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(majorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminSchoolService.createExamSubject(1L, request));
    }

    @Test
    void testCreateExamSubjectMajorBelongsToDifferentSchool() {
        Major differentSchoolMajor = Major.builder()
                .id(2L)
                .schoolId(2L)
                .name("Computer Science")
                .direction("Artificial Intelligence")
                .deleted(false)
                .build();

        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(2L)
                .subjectName("Data Structures")
                .subjectCode("CS101")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));
        when(majorRepository.findById(2L)).thenReturn(Optional.of(differentSchoolMajor));

        assertThrows(ValidationException.class, () -> adminSchoolService.createExamSubject(1L, request));
    }

    @Test
    void testCreateExamSubjectWithEmptySubjectName() {
        ExamSubjectCreateRequest request = ExamSubjectCreateRequest.builder()
                .majorId(1L)
                .subjectName("")
                .subjectCode("CS101")
                .build();

        when(schoolRepository.findById(1L)).thenReturn(Optional.of(school));

        assertThrows(ValidationException.class, () -> adminSchoolService.createExamSubject(1L, request));
    }

}
