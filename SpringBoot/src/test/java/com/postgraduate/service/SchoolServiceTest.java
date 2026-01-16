package com.postgraduate.service;

import com.postgraduate.dto.ExamSubjectDTO;
import com.postgraduate.dto.MajorDTO;
import com.postgraduate.dto.SchoolDTO;
import com.postgraduate.dto.SchoolDetailDTO;
import com.postgraduate.dto.SchoolRequirementDTO;
import com.postgraduate.entity.ExamSubject;
import com.postgraduate.entity.Major;
import com.postgraduate.entity.School;
import com.postgraduate.entity.SchoolRequirement;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.ExamSubjectRepository;
import com.postgraduate.repository.MajorRepository;
import com.postgraduate.repository.SchoolRepository;
import com.postgraduate.repository.SchoolRequirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for SchoolService.
 * Tests school search and filtering functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SchoolServiceTest {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ExamSubjectRepository examSubjectRepository;

    @Autowired
    private SchoolRequirementRepository schoolRequirementRepository;

    private School school1;
    private School school2;
    private School school3;
    private Major major1;
    private ExamSubject examSubject1;
    private SchoolRequirement requirement1;

    @BeforeEach
    void setUp() {
        // Clear existing data
        schoolRepository.deleteAll();
        majorRepository.deleteAll();
        examSubjectRepository.deleteAll();
        schoolRequirementRepository.deleteAll();

        // Create test schools
        school1 = School.builder()
                .name("Tsinghua University")
                .city("Beijing")
                .tier("985")
                .website("https://www.tsinghua.edu.cn")
                .intro("Top university in China")
                .deleted(false)
                .build();
        school1 = schoolRepository.save(school1);

        school2 = School.builder()
                .name("Nanjing University")
                .city("Nanjing")
                .tier("985")
                .website("https://www.nju.edu.cn")
                .intro("Leading university in Jiangsu")
                .deleted(false)
                .build();
        school2 = schoolRepository.save(school2);

        school3 = School.builder()
                .name("Jiangsu University")
                .city("Zhenjiang")
                .tier("211")
                .website("https://www.ujs.edu.cn")
                .intro("Regional university")
                .deleted(false)
                .build();
        school3 = schoolRepository.save(school3);

        // Create test major
        major1 = Major.builder()
                .schoolId(school1.getId())
                .name("Computer Science")
                .direction("AI")
                .deleted(false)
                .build();
        major1 = majorRepository.save(major1);

        // Create test exam subject
        examSubject1 = ExamSubject.builder()
                .schoolId(school1.getId())
                .majorId(major1.getId())
                .subjectName("Math")
                .subjectCode("101")
                .deleted(false)
                .build();
        examSubject1 = examSubjectRepository.save(examSubject1);

        // Create test requirement
        requirement1 = SchoolRequirement.builder()
                .schoolId(school1.getId())
                .majorId(major1.getId())
                .content("Reexamination requirement 1")
                .updatedBy("admin")
                .deleted(false)
                .build();
        requirement1 = schoolRequirementRepository.save(requirement1);
    }

    @Test
    void testSearchSchoolsWithoutFilters() {
        Page<SchoolDTO> result = schoolService.searchSchools(null, null, null, null, null, 0, null);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testSearchSchoolsByCity() {
        Page<SchoolDTO> result = schoolService.searchSchools("Nanjing", null, null, null, null, 0, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Nanjing University", result.getContent().get(0).getName());
    }

    @Test
    void testSearchSchoolsByTier() {
        Page<SchoolDTO> result = schoolService.searchSchools(null, "985", null, null, null, 0, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testSearchSchoolsByCityAndTier() {
        Page<SchoolDTO> result = schoolService.searchSchools("Nanjing", "985", null, null, null, 0, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Nanjing University", result.getContent().get(0).getName());
    }

    @Test
    void testSearchSchoolsWithPagination() {
        Page<SchoolDTO> result = schoolService.searchSchools(null, null, null, null, null, 0, 2);

        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getSize());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void testSearchSchoolsWithInvalidScoreRange() {
        assertThrows(ValidationException.class, () -> {
            schoolService.searchSchools(null, null, 400, 300, null, 0, null);
        });
    }

    @Test
    void testSearchSchoolsWithInvalidScoreMin() {
        assertThrows(ValidationException.class, () -> {
            schoolService.searchSchools(null, null, -10, 300, null, 0, null);
        });
    }

    @Test
    void testSearchSchoolsWithInvalidScoreMax() {
        assertThrows(ValidationException.class, () -> {
            schoolService.searchSchools(null, null, 300, 600, null, 0, null);
        });
    }

    @Test
    void testGetSchoolByIdSuccess() {
        SchoolDTO result = schoolService.getSchoolById(school1.getId());

        assertNotNull(result);
        assertEquals(school1.getId(), result.getId());
        assertEquals("Tsinghua University", result.getName());
        assertEquals("Beijing", result.getCity());
        assertEquals("985", result.getTier());
    }

    @Test
    void testGetSchoolByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            schoolService.getSchoolById(999L);
        });
    }

    @Test
    void testSearchSchoolsExcludesSoftDeletedRecords() {
        // Soft delete one school
        school1.setDeleted(true);
        schoolRepository.save(school1);

        Page<SchoolDTO> result = schoolService.searchSchools(null, null, null, null, null, 0, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertFalse(result.getContent().stream()
                .anyMatch(s -> s.getId().equals(school1.getId())));
    }

    @Test
    void testSearchSchoolsWithDefaultPageSize() {
        // Create many schools to test default page size
        for (int i = 0; i < 25; i++) {
            School school = School.builder()
                    .name("School " + i)
                    .city("City " + i)
                    .tier("985")
                    .deleted(false)
                    .build();
            schoolRepository.save(school);
        }

        Page<SchoolDTO> result = schoolService.searchSchools(null, null, null, null, null, 0, null);

        assertNotNull(result);
        assertEquals(20, result.getSize());
    }

    @Test
    void testSearchSchoolsWithMaxPageSize() {
        Page<SchoolDTO> result = schoolService.searchSchools(null, null, null, null, null, 0, 200);

        assertNotNull(result);
        assertEquals(20, result.getSize());
    }

    @Test
    void testGetSchoolDetailSuccess() {
        SchoolDetailDTO result = schoolService.getSchoolDetail(school1.getId());

        assertNotNull(result);
        assertEquals(school1.getId(), result.getId());
        assertEquals("Tsinghua University", result.getName());
        assertEquals(1, result.getMajors().size());
        assertEquals("Computer Science", result.getMajors().get(0).getName());
        assertEquals(1, result.getExamSubjects().size());
        assertEquals("Math", result.getExamSubjects().get(0).getSubjectName());
        assertEquals(1, result.getRequirements().size());
        assertEquals("Reexamination requirement 1", result.getRequirements().get(0).getContent());
    }

    @Test
    void testGetSchoolDetailNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            schoolService.getSchoolDetail(999L);
        });
    }

    @Test
    void testGetSchoolDetailWithNoMajors() {
        SchoolDetailDTO result = schoolService.getSchoolDetail(school2.getId());

        assertNotNull(result);
        assertEquals(school2.getId(), result.getId());
        assertEquals(0, result.getMajors().size());
        assertEquals(0, result.getExamSubjects().size());
        assertEquals(0, result.getRequirements().size());
    }

    @Test
    void testGetSchoolRequirementsSuccess() {
        java.util.List<SchoolRequirementDTO> result = schoolService.getSchoolRequirements(school1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Reexamination requirement 1", result.get(0).getContent());
    }

    @Test
    void testGetSchoolRequirementsNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            schoolService.getSchoolRequirements(999L);
        });
    }

    @Test
    void testGetSchoolRequirementsEmpty() {
        java.util.List<SchoolRequirementDTO> result = schoolService.getSchoolRequirements(school2.getId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetSchoolExamSubjectsSuccess() {
        java.util.List<ExamSubjectDTO> result = schoolService.getSchoolExamSubjects(school1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Math", result.get(0).getSubjectName());
        assertEquals("101", result.get(0).getSubjectCode());
    }

    @Test
    void testGetSchoolExamSubjectsNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            schoolService.getSchoolExamSubjects(999L);
        });
    }

    @Test
    void testGetSchoolExamSubjectsEmpty() {
        java.util.List<ExamSubjectDTO> result = schoolService.getSchoolExamSubjects(school2.getId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testGetSchoolDetailExcludesSoftDeletedRelations() {
        // Soft delete the major
        major1.setDeleted(true);
        majorRepository.save(major1);

        SchoolDetailDTO result = schoolService.getSchoolDetail(school1.getId());

        assertNotNull(result);
        assertEquals(0, result.getMajors().size());
    }
