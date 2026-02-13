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

    /**
     * 获取当前管理员ID（从请求头或session中获取）
     * 实际应用中应该从认证信息中获取
     */
    private Long getCurrentAdminId() {
        // 这里简化处理，实际应该从认证信息中获取
        return 1L; // 默认返回1，实际应该从认证系统获取
    }

    /**
     * 上传原创作品
     * POST /api/works
     */
    @PostMapping
    public ApiResponse<?> uploadCreativeWork(@RequestBody CreativeWork creativeWork) {
        // 验证必填字段
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

    /**
     * 获取原创作品列表（已审核通过的作品）
     * GET /api/works
     */
    @GetMapping
    public ApiResponse<?> getCreativeWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreativeWork> works = creativeWorkService.getCreativeWorks(pageable);
        return ApiResponse.success(works);
    }

    /**
     * 获取作品详情
     * GET /api/works/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<?> getCreativeWorkDetail(@PathVariable Long id) {
        CreativeWork work = creativeWorkService.getCreativeWorkById(id);
        if (work == null) {
            return ApiResponse.error(3301, "作品不存在");
        }
        return ApiResponse.success(work);
    }

    /**
     * 删除作品
     * DELETE /api/works/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCreativeWork(@PathVariable Long id) {
        CreativeWork work = creativeWorkService.getCreativeWorkById(id);
        if (work == null) {
            return ApiResponse.error(3301, "作品不存在");
        }

        creativeWorkService.deleteCreativeWork(id);
        return ApiResponse.success("删除成功");
    }

    /**
     * 获取待审核作品列表（管理员）
     * GET /api/works/pending
     */
    @GetMapping("/pending")
    public ApiResponse<?> getPendingCreativeWorks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CreativeWork> works = creativeWorkService.getPendingCreativeWorks(pageable);
        return ApiResponse.success(works);
    }

    /**
     * 审核作品（管理员，通过/拒绝）
     * PUT /api/works/{id}/review
     */
    @PutMapping("/{id}/review")
    public ApiResponse<?> reviewCreativeWork(
            @PathVariable Long id,
            @RequestParam String status) {
        
        // 验证状态参数
        CreativeWork.WorkStatus workStatus;
        try {
            workStatus = CreativeWork.WorkStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, "无效的审核状态，必须为 APPROVED 或 REJECTED");
        }

        // 只允许通过或拒绝状态
        if (workStatus != CreativeWork.WorkStatus.APPROVED && workStatus != CreativeWork.WorkStatus.REJECTED) {
            return ApiResponse.error(400, "无效的审核状态，必须为 APPROVED 或 REJECTED");
        }

        try {
            CreativeWork reviewedWork = creativeWorkService.reviewCreativeWork(id, workStatus);
            if (reviewedWork == null) {
                return ApiResponse.error(3301, "作品不存在");
            }

            // 记录管理员操作日志
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
