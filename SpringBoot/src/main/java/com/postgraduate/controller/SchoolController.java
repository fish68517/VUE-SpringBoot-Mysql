package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.ExamSubjectDTO;
import com.postgraduate.dto.SchoolDTO;
import com.postgraduate.dto.SchoolDetailDTO;
import com.postgraduate.dto.SchoolRequirementDTO;
import com.postgraduate.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postgraduate.dto.SchoolStatsDTO; // 新增
import com.postgraduate.service.StatisticsService; // 新增

import java.util.List;

/**
 * REST Controller for school search and detail endpoints.
 * Handles school search with filtering, pagination, and detail retrieval.
 */
@Slf4j
@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private StatisticsService statisticsService; // 注入统计服务

    

    /**
     * Search schools with optional filters and pagination.
     * Supports filtering by city, tier, expected score range, and major.
     * Default page size is 20, maximum page size is 100.
     *
     * @param city optional city filter
     * @param tier optional school tier filter (985/211/DOUBLE_NON/OTHER)
     * @param expectedScoreMin optional minimum expected score
     * @param expectedScoreMax optional maximum expected score
     * @param major optional major filter
     * @param page page number (0-indexed, default 0)
     * @param size page size (default 20, max 100)
     * @return ResponseEntity with ApiResponse containing paginated school results
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SchoolDTO>>> searchSchools(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String tier,
            @RequestParam(required = false) Integer expectedScoreMin,
            @RequestParam(required = false) Integer expectedScoreMax,
            @RequestParam(required = false) String major,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        log.info("Search schools endpoint called with filters - city: {}, tier: {}, scoreMin: {}, scoreMax: {}, major: {}, page: {}, size: {}",
                city, tier, expectedScoreMin, expectedScoreMax, major, page, size);

        Page<SchoolDTO> schools = schoolService.searchSchools(city, tier, expectedScoreMin, expectedScoreMax, major, page, size);
        return ResponseEntity.ok(ApiResponse.success("Schools retrieved successfully", schools));
    }

    /**
     * Get detailed information for a specific school by ID.
     * Includes school information, majors, exam subjects, and requirements.
     *
     * @param id the school ID
     * @return ResponseEntity with ApiResponse containing the school details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SchoolDetailDTO>> getSchoolDetail(@PathVariable Long id) {
        log.info("Get school detail endpoint called for school id: {}", id);
        SchoolDetailDTO school = schoolService.getSchoolDetail(id);
        return ResponseEntity.ok(ApiResponse.success("School details retrieved successfully", school));
    }

    /**
     * Get reexamination requirements for a specific school.
     *
     * @param id the school ID
     * @return ResponseEntity with ApiResponse containing the school requirements
     */
    @GetMapping("/{id}/requirements")
    public ResponseEntity<ApiResponse<List<SchoolRequirementDTO>>> getSchoolRequirements(@PathVariable Long id) {
        log.info("Get school requirements endpoint called for school id: {}", id);
        List<SchoolRequirementDTO> requirements = schoolService.getSchoolRequirements(id);
        return ResponseEntity.ok(ApiResponse.success("School requirements retrieved successfully", requirements));
    }

    /**
     * Get exam subjects for a specific school.
     *
     * @param id the school ID
     * @return ResponseEntity with ApiResponse containing the exam subjects
     */
    @GetMapping("/{id}/subjects")
    public ResponseEntity<ApiResponse<List<ExamSubjectDTO>>> getSchoolExamSubjects(@PathVariable Long id) {
        log.info("Get school exam subjects endpoint called for school id: {}", id);
        List<ExamSubjectDTO> subjects = schoolService.getSchoolExamSubjects(id);
        return ResponseEntity.ok(ApiResponse.success("School exam subjects retrieved successfully", subjects));
    }


    // --- 新增接口开始 ---
    @GetMapping("/{id}/stats")
    public ApiResponse<SchoolStatsDTO> getSchoolStats(@PathVariable Long id) {
        SchoolStatsDTO stats = statisticsService.getSchoolStats(id);
        return ApiResponse.success("Fetched school statistics", stats);
    }
    // --- 新增接口结束 ---

}
