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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员端：院校管理接口
 * 说明：包含院校的增删改查，以及专业、考试科目的新增等。
 * 权限：所有接口均要求 ADMIN 角色。
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/schools")
public class AdminSchoolController {

    @Autowired
    private AdminSchoolService adminSchoolService;

    /**
     * 【新增】分页查询院校列表
     * 前端调用示例：
     * GET /api/admin/schools?page=0&size=20
     *
     * @param page 页码（从0开始）
     * @param size 每页条数
     * @return 分页后的院校列表（Page结构里包含 content/totalElements/totalPages/number 等）
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<SchoolDTO>>> getSchools(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // 为了稳定输出，默认按 id 倒序（你也可以改成 createdAt）
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        log.info("Get schools endpoint called - page: {}, size: {}", page, size);

        Page<SchoolDTO> result = adminSchoolService.getSchools(pageable);
        return ResponseEntity.ok(ApiResponse.success("Schools retrieved successfully", result));
    }

    /**
     * 创建学校
     * 管理员可创建学校：名称、城市、层次、官网、简介
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
     * 更新学校
     * 管理员可更新学校：名称、城市、层次、官网、简介
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
     * 删除学校（软删除）
     * 说明：标记删除，不做物理删除
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchool(@PathVariable("id") Long schoolId) {
        log.info("Delete school endpoint called - schoolId: {}", schoolId);
        adminSchoolService.deleteSchool(schoolId);
        return ResponseEntity.ok(ApiResponse.success("School deleted successfully", null));
    }

    /**
     * 为某学校新增专业
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
     * 为某学校新增考试科目
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
