package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Announcement;
import com.tourism.service.AnnouncementService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 公告管理控制器
 */
@RestController
@RequestMapping("/announcements")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AnnouncementController {
    
    private static final Logger logger = LoggerUtil.getLogger(AnnouncementController.class);
    
    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * 创建公告端点（管理员）
     * @param request 创建公告请求体
     * @return API响应
     */
    @PostMapping("/admin")
    public ApiResponse<Map<String, Object>> createAnnouncement(@RequestBody CreateAnnouncementRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建公告请求 - 标题: " + request.getTitle() + 
                ", 创建者ID: " + request.getCreatedBy());
            
            Announcement announcement = announcementService.createAnnouncement(
                request.getTitle(),
                request.getContent(),
                request.getCreatedBy()
            );
            
            Map<String, Object> response = buildAnnouncementResponse(announcement);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建公告失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有公告端点
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取公告列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            Page<Announcement> announcementPage = announcementService.getAllAnnouncements(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("announcements", announcementPage.getContent().stream()
                .map(this::buildAnnouncementResponse)
                .collect(Collectors.toList()));
            response.put("total", announcementPage.getTotalElements());
            response.put("currentPage", announcementPage.getNumber());
            response.put("pageSize", announcementPage.getSize());
            response.put("totalPages", announcementPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取公告列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取公告详情端点
     * @param id 公告ID
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getAnnouncementById(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理获取公告详情请求 - ID: " + id);
            
            Optional<Announcement> announcementOptional = announcementService.findById(id);
            if (!announcementOptional.isPresent()) {
                LoggerUtil.warn(logger, "获取公告详情失败：公告不存在 - ID: " + id);
                return ApiResponse.error("公告不存在");
            }
            
            Map<String, Object> response = buildAnnouncementResponse(announcementOptional.get());
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取公告详情失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新公告端点（管理员）
     * @param id 公告ID
     * @param request 更新公告请求体
     * @return API响应
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<Map<String, Object>> updateAnnouncement(
            @PathVariable Long id,
            @RequestBody UpdateAnnouncementRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新公告请求 - ID: " + id + ", 标题: " + request.getTitle());
            
            Announcement announcement = announcementService.updateAnnouncement(
                id,
                request.getTitle(),
                request.getContent()
            );
            
            Map<String, Object> response = buildAnnouncementResponse(announcement);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新公告失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除公告端点（管理员）
     * @param id 公告ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteAnnouncement(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除公告请求 - ID: " + id);
            
            announcementService.deleteAnnouncement(id);
            
            return ApiResponse.success("公告删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除公告失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建公告响应对象
     * @param announcement 公告对象
     * @return 公告响应Map
     */
    private Map<String, Object> buildAnnouncementResponse(Announcement announcement) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", announcement.getId());
        response.put("title", announcement.getTitle());
        response.put("content", announcement.getContent());
        response.put("createdBy", announcement.getCreatedBy());
        response.put("createdAt", announcement.getCreatedAt());
        response.put("updatedAt", announcement.getUpdatedAt());
        return response;
    }
    
    /**
     * 创建公告请求体
     */
    public static class CreateAnnouncementRequest {
        private String title;
        private String content;
        private Long createdBy;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public Long getCreatedBy() {
            return createdBy;
        }
        
        public void setCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
        }
    }
    
    /**
     * 更新公告请求体
     */
    public static class UpdateAnnouncementRequest {
        private String title;
        private String content;
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}
