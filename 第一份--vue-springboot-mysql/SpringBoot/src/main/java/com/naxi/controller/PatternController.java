package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.Pattern;
import com.naxi.service.PatternService;
import com.naxi.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patterns")
public class PatternController {
    @Autowired
    private PatternService patternService;

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
     * 获取纹样列表（支持分页、分类筛选）
     */
    @GetMapping
    public ApiResponse<?> getPatterns(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Pattern> patterns;
        
        if (category != null && !category.isEmpty()) {
            try {
                Pattern.PatternCategory patternCategory = Pattern.PatternCategory.valueOf(category);
                patterns = patternService.getPatternsByCategory(patternCategory, pageable);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(400, "无效的分类");
            }
        } else {
            patterns = patternService.getPatterns(pageable);
        }
        return ApiResponse.success(patterns);
    }

    /**
     * 获取纹样详情
     */
    @GetMapping("/{id}")
    public ApiResponse<?> getPattern(@PathVariable Long id) {
        Pattern pattern = patternService.getPatternById(id);
        if (pattern != null) {
            return ApiResponse.success(pattern);
        }
        return ApiResponse.error(2001, "纹样不存在");
    }

    /**
     * 创建纹样（管理员）
     */
    @PostMapping
    public ApiResponse<?> createPattern(@RequestBody Pattern pattern) {
        if (pattern.getName() == null || pattern.getName().isEmpty()) {
            return ApiResponse.error(400, "纹样名称不能为空");
        }
        if (pattern.getCategory() == null) {
            return ApiResponse.error(400, "纹样分类不能为空");
        }
        if (pattern.getImageUrl() == null || pattern.getImageUrl().isEmpty()) {
            return ApiResponse.error(400, "纹样图片URL不能为空");
        }
        
        Pattern savedPattern = patternService.savePattern(pattern);

        // 记录管理员操作日志
        Long adminId = getCurrentAdminId();
        systemService.recordAdminAuditLog(
            adminId,
            "CREATE",
            "Pattern",
            savedPattern.getId(),
            "创建纹样: " + pattern.getName()
        );

        return ApiResponse.success("创建成功", savedPattern);
    }

    /**
     * 更新纹样信息（管理员）
     */
    @PutMapping("/{id}")
    public ApiResponse<?> updatePattern(@PathVariable Long id, @RequestBody Pattern pattern) {
        Pattern updatedPattern = patternService.updatePattern(id, pattern);
        if (updatedPattern != null) {
            // 记录管理员操作日志
            Long adminId = getCurrentAdminId();
            systemService.recordAdminAuditLog(
                adminId,
                "UPDATE",
                "Pattern",
                id,
                "更新纹样: " + pattern.getName()
            );

            return ApiResponse.success("更新成功", updatedPattern);
        }
        return ApiResponse.error(2001, "纹样不存在");
    }

    /**
     * 删除纹样（管理员）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<?> deletePattern(@PathVariable Long id) {
        Pattern pattern = patternService.getPatternById(id);
        if (pattern == null) {
            return ApiResponse.error(2001, "纹样不存在");
        }
        patternService.deletePattern(id);

        // 记录管理员操作日志
        Long adminId = getCurrentAdminId();
        systemService.recordAdminAuditLog(
            adminId,
            "DELETE",
            "Pattern",
            id,
            "删除纹样: " + pattern.getName()
        );

        return ApiResponse.success("删除成功");
    }

    /**
     * 搜索纹样（按名称、文化内涵关键词）
     */
    @GetMapping("/search")
    public ApiResponse<?> searchPatterns(
            @RequestParam String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        if (keyword == null || keyword.isEmpty()) {
            return ApiResponse.error(400, "搜索关键词不能为空");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Pattern> patterns;
        
        if (category != null && !category.isEmpty()) {
            try {
                Pattern.PatternCategory patternCategory = Pattern.PatternCategory.valueOf(category);
                patterns = patternService.searchPatternsByCategoryAndKeyword(patternCategory, keyword, pageable);
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(400, "无效的分类");
            }
        } else {
            patterns = patternService.searchPatterns(keyword, pageable);
        }
        
        return ApiResponse.success(patterns);
    }

    /**
     * 下载纹样素材
     */
    @GetMapping("/{id}/download")
    public ApiResponse<?> downloadPattern(@PathVariable Long id) {
        Pattern pattern = patternService.getPatternById(id);
        if (pattern == null) {
            return ApiResponse.error(2001, "纹样不存在");
        }
        
        if (pattern.getDownloadUrl() == null || pattern.getDownloadUrl().isEmpty()) {
            return ApiResponse.error(400, "纹样下载链接不可用");
        }
        
        // 记录下载量
        patternService.incrementDownloadCount(id);
        
        return ApiResponse.success("下载链接", pattern.getDownloadUrl());
    }
}
