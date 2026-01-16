package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.ExamSubjectCreateRequest;
import com.postgraduate.dto.ExamSubjectDTO;
import com.postgraduate.dto.MajorCreateRequest;
import com.postgraduate.dto.MajorDTO;
import com.postgraduate.dto.SchoolCreateRequest;
import com.postgraduate.dto.SchoolDTO;
import com.postgraduate.dto.SchoolUpdateRequest;
import com.postgraduate.service.AdminSchoolService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for admin school management endpoints.
 * Handles school CRUD operations including creation, update, and deletion.
 * All endpoints require ADMIN role.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/schools")
public class AdminSchoolController {

    @Autowired
    private AdminSchoolService adminSchoolService;

    /**
     * Create a new school.
     * Admin can create schools with name, city, tier, website, and introduction.
     *
     * @param request the school creation request containing school details
     * @return ResponseEntity with ApiResponse containing the created school
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SchoolDTO>> createSchool(@Valid @RequestBody SchoolCreateRequest request) {
        log.info("Create school endpoint called - name: {}, city: {}, tier: {}", 
                request.getName(), request.getCity(), request.getTier());
        SchoolDTO school = adminSchoolService.createSchool(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("School created successfully", school));
    }

    /**
     * Update an existing school.
     * Admin can update school name, city, tier, website, and introduction.
     *
     * @param schoolId the ID of the school to update
     * @param request the school update request containing updated details
     * @return ResponseEntity with ApiResponse containing the updated school
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SchoolDTO>> updateSchool(
            @PathVariable("id") Long schoolId,
            @Valid @RequestBody SchoolUpdateRequest request) {
        log.info("Update school endpoint called - schoolId: {}, name: {}, city: {}, tier: {}", 
                schoolId, request.getName(), request.getCity(), request.getTier());
        SchoolDTO school = adminSchoolService.updateSchool(schoolId, request);
        return ResponseEntity.ok(ApiResponse.success("School updated successfully", school));
    }

    /**
     * Delete a school (soft delete).
     * Admin can delete schools. The school is marked as deleted rather than permanently removed.
     *
     * @param schoolId the ID of the school to delete
     * @return ResponseEntity with ApiResponse indicating successful deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable("id") Long schoolId) {
        log.info("Delete school endpoint called - schoolId: {}", schoolId);
        adminSchoolService.deleteSchool(schoolId);
        return ResponseEntity.ok(ApiResponse.success("School deleted successfully", null));
    }

    /**
     * Create a new major for a school.
     * Admin can create majors with name and direction.
     *
     * @param schoolId the ID of the school
     * @param request the major creation request containing major details
     * @return ResponseEntity with ApiResponse containing the created major
     */
    @PostMapping("/{id}/majors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MajorDTO>> createMajor(
            @PathVariable("id") Long schoolId,
            @Valid @RequestBody MajorCreateRequest request) {
        log.info("Create major endpoint called - schoolId: {}, name: {}, direction: {}", 
                schoolId, request.getName(), request.getDirection());
        MajorDTO major = adminSchoolService.createMajor(schoolId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Major created successfully", major));
    }

    /**
     * Create a new exam subject for a school.
     * Admin can create exam subjects with subject name, code, and optional major association.
     *
     * @param schoolId the ID of the school
     * @param request the exam subject creation request containing subject details
     * @return ResponseEntity with ApiResponse containing the created exam subject
     */
    @PostMapping("/{id}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExamSubjectDTO>> createExamSubject(
            @PathVariable("id") Long schoolId,
            @Valid @RequestBody ExamSubjectCreateRequest request) {
        log.info("Create exam subject endpoint called - schoolId: {}, majorId: {}, subjectName: {}, subjectCode: {}", 
                schoolId, request.getMajorId(), request.getSubjectName(), request.getSubjectCode());
        ExamSubjectDTO examSubject = adminSchoolService.createExamSubject(schoolId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam subject created successfully", examSubject));
    }

}
