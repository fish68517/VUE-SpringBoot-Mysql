package com.postgraduate.service;

import com.postgraduate.dto.*;
import com.postgraduate.entity.School;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.ExamSubjectRepository;
import com.postgraduate.repository.MajorRepository;
import com.postgraduate.repository.SchoolRepository;
import com.postgraduate.repository.SchoolRequirementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling school search and filtering operations.
 * Provides methods for searching schools with various filter criteria and retrieving detailed information.
 */
@Slf4j
@Service
public class SchoolService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ExamSubjectRepository examSubjectRepository;

    @Autowired
    private SchoolRequirementRepository schoolRequirementRepository;

    /**
     * Search schools with optional filters for city, tier, expected score range, and major.
     * Supports pagination with default page size of 20.
     *
     * @param city optional city filter
     * @param tier optional school tier filter (985/211/DOUBLE_NON/OTHER)
     * @param expectedScoreMin optional minimum expected score
     * @param expectedScoreMax optional maximum expected score
     * @param major optional major filter (not directly used in this version, for future enhancement)
     * @param page page number (0-indexed)
     * @param size page size (default 20 if not specified or exceeds max)
     * @return Page of SchoolDTO matching the filter criteria
     * @throws ValidationException if filter parameters are invalid
     */
    @Transactional(readOnly = true)
    public Page<SchoolDTO> searchSchools(String city, String tier, Integer expectedScoreMin,
                                         Integer expectedScoreMax, String major, int page, Integer size) {
        log.info("Searching schools with filters - city: {}, tier: {}, scoreMin: {}, scoreMax: {}, major: {}",
                city, tier, expectedScoreMin, expectedScoreMax, major);

        // Validate and set page size
        int pageSize = (size == null || size <= 0 || size > 100) ? DEFAULT_PAGE_SIZE : size;
        Pageable pageable = PageRequest.of(page, pageSize);

        // Validate score range if provided
        if (expectedScoreMin != null && expectedScoreMax != null) {
            if (expectedScoreMin > expectedScoreMax) {
                throw new ValidationException("Expected score minimum cannot be greater than maximum");
            }
            if (expectedScoreMin < 0 || expectedScoreMax > 500) {
                throw new ValidationException("Expected score must be between 0 and 500");
            }
        }

        // Build specification for dynamic filtering
        Specification<School> spec = buildSearchSpecification(city, tier, expectedScoreMin, expectedScoreMax);

        // Execute query
        Page<School> schools = schoolRepository.findAll(spec, pageable);
        log.info("Found {} schools matching criteria", schools.getTotalElements());

        return schools.map(SchoolDTO::fromEntity);
    }

    /**
     * Get school details by ID.
     *
     * @param schoolId the school ID
     * @return SchoolDTO containing the school information
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional(readOnly = true)
    public SchoolDTO getSchoolById(Long schoolId) {
        log.info("Retrieving school details for school id: {}", schoolId);
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));
        return SchoolDTO.fromEntity(school);
    }

    /**
     * Get detailed school information including majors, exam subjects, and requirements.
     *
     * @param schoolId the school ID
     * @return SchoolDetailDTO containing comprehensive school information
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional(readOnly = true)
    public SchoolDetailDTO getSchoolDetail(Long schoolId) {
        log.info("Retrieving detailed school information for school id: {}", schoolId);
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        List<MajorDTO> majors = majorRepository.findBySchoolIdAndDeletedFalse(schoolId)
                .stream()
                .map(MajorDTO::fromEntity)
                .collect(Collectors.toList());

        List<ExamSubjectDTO> examSubjects = examSubjectRepository.findBySchoolIdAndDeletedFalse(schoolId)
                .stream()
                .map(ExamSubjectDTO::fromEntity)
                .collect(Collectors.toList());

        List<SchoolRequirementDTO> requirements = schoolRequirementRepository.findBySchoolIdAndDeletedFalse(schoolId)
                .stream()
                .map(SchoolRequirementDTO::fromEntity)
                .collect(Collectors.toList());

        return SchoolDetailDTO.fromEntity(school, majors, examSubjects, requirements);
    }

    /**
     * Get reexamination requirements for a specific school.
     *
     * @param schoolId the school ID
     * @return List of SchoolRequirementDTO for the school
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional(readOnly = true)
    public List<SchoolRequirementDTO> getSchoolRequirements(Long schoolId) {
        log.info("Retrieving requirements for school id: {}", schoolId);
        // Verify school exists
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        return schoolRequirementRepository.findBySchoolIdAndDeletedFalse(schoolId)
                .stream()
                .map(SchoolRequirementDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get exam subjects for a specific school.
     *
     * @param schoolId the school ID
     * @return List of ExamSubjectDTO for the school
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional(readOnly = true)
    public List<ExamSubjectDTO> getSchoolExamSubjects(Long schoolId) {
        log.info("Retrieving exam subjects for school id: {}", schoolId);
        // Verify school exists
        schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        return examSubjectRepository.findBySchoolIdAndDeletedFalse(schoolId)
                .stream()
                .map(ExamSubjectDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Build JPA Specification for dynamic filtering based on provided criteria.
     *
     * @param city optional city filter
     * @param tier optional school tier filter
     * @param expectedScoreMin optional minimum expected score
     * @param expectedScoreMax optional maximum expected score
     * @return Specification for querying schools
     */
    private Specification<School> buildSearchSpecification(String city, String tier,
                                                           Integer expectedScoreMin, Integer expectedScoreMax) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<>();

            // Always filter out soft-deleted records
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Add city filter if provided
            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), city));
            }

            // Add tier filter if provided
            if (tier != null && !tier.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tier"), tier));
            }

            // Note: expectedScoreMin and expectedScoreMax are validated but not directly used
            // in the current schema as schools don't have a cutoff score field.
            // This is prepared for future enhancement when cutoff score is added to School entity.

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

}
