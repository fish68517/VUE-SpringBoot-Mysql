package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.CreativeWork;
import com.naxi.service.CreativeWorkService;
import com.naxi.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/works")
public class CreativeWorkController {
    @Autowired
    private CreativeWorkService creativeWorkService;

    @Autowired
    private SystemService systemService;

    private Long getCurrentAdminId() {
        return 1L;
    }

    @PostMapping
    public ApiResponse<?> uploadCreativeWork(@RequestBody CreativeWork creativeWork) {
        if (creativeWork.getUserId() == null) {
            return ApiResponse.error(400, "用户ID不能为空");
        }
        if (creativeWork.getTitle() == null || creativeWork.getTitle().isEmpty()) {
            return ApiResponse.error(400, "作品标题不能为空");
        }
        if (creativeWork.getImageUrl() == null || creativeWork.getImageUrl().isEmpty()) {
            return ApiResponse.error(400, "作品图片URL不能为空");
        }

        try {
            CreativeWork savedWork = creativeWorkService.uploadCreativeWork(creativeWork);
            return ApiResponse.success("上传成功，待审核", savedWork);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<?> getCreativeWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreativeWork> works;

        if (userId != null) {
            if (status != null && !status.isBlank()) {
                try {
                    CreativeWork.WorkStatus workStatus = CreativeWork.WorkStatus.valueOf(status.toUpperCase());
                    works = creativeWorkService.getUserCreativeWorks(userId, workStatus, pageable);
                } catch (IllegalArgumentException e) {
                    return ApiResponse.error(400, "无效的作品状态");
                }
            } else {
                works = creativeWorkService.getUserCreativeWorks(userId, pageable);
            }
        } else if (status != null && !status.isBlank()) {
            try {
                CreativeWork.WorkStatus workStatus = CreativeWork.WorkStatus.valueOf(status.toUpperCase());
                works = creativeWorkService.getCreativeWorks(workStatus, pageable);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(400, "无效的作品状态");
            }
        } else {
            works = creativeWorkService.getCreativeWorks(pageable);
        }

        return ApiResponse.success(works);
    }

    @GetMapping("/all")
    public ApiResponse<?> getAllCreativeWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreativeWork> works = creativeWorkService.getAllCreativeWorks(pageable);
        return ApiResponse.success(works);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getCreativeWorkDetail(@PathVariable Long id) {
        CreativeWork work = creativeWorkService.getCreativeWorkById(id);
        if (work == null) {
            return ApiResponse.error(3301, "作品不存在");
        }
        return ApiResponse.success(work);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCreativeWork(@PathVariable Long id) {
        CreativeWork work = creativeWorkService.getCreativeWorkById(id);
        if (work == null) {
            return ApiResponse.error(3301, "作品不存在");
        }

        creativeWorkService.deleteCreativeWork(id);
        return ApiResponse.success("删除成功");
    }

    @GetMapping("/pending")
    public ApiResponse<?> getPendingCreativeWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreativeWork> works = creativeWorkService.getPendingCreativeWorks(pageable);
        return ApiResponse.success(works);
    }

    @PutMapping("/{id}/review")
    public ApiResponse<?> reviewCreativeWork(
            @PathVariable Long id,
            @RequestParam String status) {
        CreativeWork.WorkStatus workStatus;
        try {
            workStatus = CreativeWork.WorkStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, "无效的审核状态，必须为 APPROVED 或 REJECTED");
        }

        if (workStatus != CreativeWork.WorkStatus.APPROVED && workStatus != CreativeWork.WorkStatus.REJECTED) {
            return ApiResponse.error(400, "无效的审核状态，必须为 APPROVED 或 REJECTED");
        }

        try {
            CreativeWork reviewedWork = creativeWorkService.reviewCreativeWork(id, workStatus);
            if (reviewedWork == null) {
                return ApiResponse.error(3301, "作品不存在");
            }

            Long adminId = getCurrentAdminId();
            systemService.recordAdminAuditLog(
                    adminId,
                    "REVIEW",
                    "CreativeWork",
                    id,
                    "审核作品: " + reviewedWork.getTitle() + "，状态: " + status
            );

            return ApiResponse.success("审核成功", reviewedWork);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
