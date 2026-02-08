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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service for handling admin school management operations.
 * Manages school CRUD operations including creation, update, and deletion.
 */
@Slf4j
@Service
public class AdminSchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private ExamSubjectRepository examSubjectRepository;

    /**
     * Create a new school.
     *
     * @param request the school creation request containing school details
     * @return SchoolDTO containing the created school information
     * @throws ValidationException if school data is invalid
     */
    @Transactional
    public SchoolDTO createSchool(SchoolCreateRequest request) {
        log.info("Creating new school - name: {}, city: {}, tier: {}", request.getName(), request.getCity(), request.getTier());

        // Validate school data
        validateSchoolData(request.getName(), request.getCity(), request.getTier());

        // Create new school entity
        School school = School.builder()
                .name(request.getName())
                .city(request.getCity())
                .tier(request.getTier())
                .website(request.getWebsite())
                .intro(request.getIntro())
                .build();

        // Save to database
        school = schoolRepository.save(school);
        log.info("School created successfully - schoolId: {}, name: {}", school.getId(), school.getName());

        return SchoolDTO.fromEntity(school);
    }

    /**
     * Update an existing school.
     *
     * @param schoolId the ID of the school to update
     * @param request the school update request containing updated details
     * @return SchoolDTO containing the updated school information
     * @throws ResourceNotFoundException if school is not found
     * @throws ValidationException if school data is invalid
     */
    @Transactional
    public SchoolDTO updateSchool(Long schoolId, SchoolUpdateRequest request) {
        log.info("Updating school - schoolId: {}, name: {}, city: {}, tier: {}", 
                schoolId, request.getName(), request.getCity(), request.getTier());

        // Find existing school
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Validate school data
        validateSchoolData(request.getName(), request.getCity(), request.getTier());

        // Update school fields
        school.setName(request.getName());
        school.setCity(request.getCity());
        school.setTier(request.getTier());
        school.setWebsite(request.getWebsite());
        school.setIntro(request.getIntro());

        // Save to database
        school = schoolRepository.save(school);
        log.info("School updated successfully - schoolId: {}, name: {}", school.getId(), school.getName());

        return SchoolDTO.fromEntity(school);
    }

    /**
     * Delete a school (soft delete).
     * Marks the school as deleted instead of permanently removing it.
     *
     * @param schoolId the ID of the school to delete
     * @throws ResourceNotFoundException if school is not found
     */
    @Transactional
    public void deleteSchool(Long schoolId) {
        log.info("Deleting school - schoolId: {}", schoolId);

        // Find existing school
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Soft delete
        school.setDeleted(true);
        schoolRepository.save(school);

        log.info("School deleted successfully - schoolId: {}", schoolId);
    }

    /**
     * Validate school data for creation and update operations.
     *
     * @param name the school name
     * @param city the school city
     * @param tier the school tier
     * @throws ValidationException if any field is invalid
     */
    private void validateSchoolData(String name, String city, String tier) {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("School name cannot be empty");
        }

        if (!StringUtils.hasText(city)) {
            throw new ValidationException("City cannot be empty");
        }

        if (!StringUtils.hasText(tier)) {
            throw new ValidationException("School tier cannot be empty");
        }

        // Validate tier is one of the allowed values
        String[] validTiers = {"985", "211", "DOUBLE_NON", "OTHER"};
        boolean isValidTier = false;
        for (String validTier : validTiers) {
            if (validTier.equalsIgnoreCase(tier)) {
                isValidTier = true;
                break;
            }
        }

        if (!isValidTier) {
            throw new ValidationException("Invalid school tier. Must be one of: 985, 211, DOUBLE_NON, OTHER");
        }
    }

    /**
     * Create a new major for a school.
     *
     * @param schoolId the ID of the school
     * @param request the major creation request containing major details
     * @return MajorDTO containing the created major information
     * @throws ResourceNotFoundException if school is not found
     * @throws ValidationException if major data is invalid
     */
    @Transactional
    public MajorDTO createMajor(Long schoolId, MajorCreateRequest request) {
        log.info("Creating new major - schoolId: {}, name: {}, direction: {}", 
                schoolId, request.getName(), request.getDirection());

        // Verify school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // Validate major data
        validateMajorData(request.getName());

        // Create new major entity
        Major major = Major.builder()
                .schoolId(schoolId)
                .name(request.getName())
                .direction(request.getDirection())

                .build();

        // Save to database
        major = majorRepository.save(major);
        log.info("Major created successfully - majorId: {}, schoolId: {}, name: {}", 
                major.getId(), schoolId, major.getName());

        return MajorDTO.fromEntity(major);
    }

    /**
     * Create a new exam subject for a school.
     *
     * @param schoolId the ID of the school
     * @param request the exam subject creation request containing subject details
     * @return ExamSubjectDTO containing the created exam subject information
     * @throws ResourceNotFoundException if school is not found or major is not found (if majorId is provided)
     * @throws ValidationException if exam subject data is invalid
     */
    @Transactional
    public ExamSubjectDTO createExamSubject(Long schoolId, ExamSubjectCreateRequest request) {
        log.info("Creating new exam subject - schoolId: {}, majorId: {}, subjectName: {}, subjectCode: {}", 
                schoolId, request.getMajorId(), request.getSubjectName(), request.getSubjectCode());

        // Verify school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with id: " + schoolId));

        // If majorId is provided, verify major exists and belongs to the school
        if (request.getMajorId() != null) {
            Major major = majorRepository.findById(request.getMajorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Major not found with id: " + request.getMajorId()));

            if (!major.getSchoolId().equals(schoolId)) {
                throw new ValidationException("Major does not belong to the specified school");
            }
        }

        // Validate exam subject data
        validateExamSubjectData(request.getSubjectName());

        // Create new exam subject entity
        ExamSubject examSubject = ExamSubject.builder()
                .schoolId(schoolId)
                .majorId(request.getMajorId())
                .subjectName(request.getSubjectName())
                .subjectCode(request.getSubjectCode())

                .build();

        // Save to database
        examSubject = examSubjectRepository.save(examSubject);
        log.info("Exam subject created successfully - examSubjectId: {}, schoolId: {}, subjectName: {}", 
                examSubject.getId(), schoolId, examSubject.getSubjectName());

        return ExamSubjectDTO.fromEntity(examSubject);
    }

    /**
     * Validate major data for creation operations.
     *
     * @param name the major name
     * @throws ValidationException if major name is invalid
     */
    private void validateMajorData(String name) {
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Major name cannot be empty");
        }
    }

    /**
     * Validate exam subject data for creation operations.
     *
     * @param subjectName the exam subject name
     * @throws ValidationException if subject name is invalid
     */
    private void validateExamSubjectData(String subjectName) {
        if (!StringUtils.hasText(subjectName)) {
            throw new ValidationException("Subject name cannot be empty");
        }
    }

}
