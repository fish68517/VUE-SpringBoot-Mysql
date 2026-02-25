package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Attraction;
import com.tourism.entity.AttractionTag;
import com.tourism.service.AttractionService;
import com.tourism.service.BrowsingHistoryService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 景点管理控制器
 */
@RestController
@RequestMapping("/attractions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AttractionController {
    
    private static final Logger logger = LoggerUtil.getLogger(AttractionController.class);
    
    @Autowired
    private AttractionService attractionService;
    
    @Autowired
    private BrowsingHistoryService browsingHistoryService;
    
    /**
     * 获取景点列表端点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getAttractionList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取景点列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            Page<Attraction> attractionPage = attractionService.getAttractionList(page, size);
            
            // 为每个景点添加标签信息
            List<Map<String, Object>> attractions = attractionPage.getContent().stream()
                .map(this::buildAttractionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("attractions", attractions);
            response.put("total", attractionPage.getTotalElements());
            response.put("currentPage", attractionPage.getNumber());
            response.put("pageSize", attractionPage.getSize());
            response.put("totalPages", attractionPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取景点列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 搜索景点端点（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchAttractions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理搜索景点请求 - 关键词: " + keyword + ", 页码: " + page);
            
            Page<Attraction> attractionPage = attractionService.searchAttractions(keyword, page, size);
            
            // 为每个景点添加标签信息
            List<Map<String, Object>> attractions = attractionPage.getContent().stream()
                .map(this::buildAttractionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("attractions", attractions);
            response.put("total", attractionPage.getTotalElements());
            response.put("currentPage", attractionPage.getNumber());
            response.put("pageSize", attractionPage.getSize());
            response.put("totalPages", attractionPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "搜索景点失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 按标签查询景点端点（分页）
     * @param tagName 标签名称
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/by-tag")
    public ApiResponse<Map<String, Object>> getAttractionsByTag(
            @RequestParam String tagName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理按标签查询景点请求 - 标签: " + tagName + ", 页码: " + page);
            
            Page<Attraction> attractionPage = attractionService.getAttractionsByTag(tagName, page, size);
            
            // 为每个景点添加标签信息
            List<Map<String, Object>> attractions = attractionPage.getContent().stream()
                .map(this::buildAttractionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("attractions", attractions);
            response.put("total", attractionPage.getTotalElements());
            response.put("currentPage", attractionPage.getNumber());
            response.put("pageSize", attractionPage.getSize());
            response.put("totalPages", attractionPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "按标签查询景点失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取广州特色景点端点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/guangzhou-special")
    public ApiResponse<Map<String, Object>> getGuangzhouSpecialAttractions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取广州特色景点请求 - 页码: " + page);
            
            Page<Attraction> attractionPage = attractionService.getGuangzhouSpecialAttractions(page, size);
            
            // 为每个景点添加标签信息
            List<Map<String, Object>> attractions = attractionPage.getContent().stream()
                .map(this::buildAttractionResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("attractions", attractions);
            response.put("total", attractionPage.getTotalElements());
            response.put("currentPage", attractionPage.getNumber());
            response.put("pageSize", attractionPage.getSize());
            response.put("totalPages", attractionPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取广州特色景点失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取景点详情端点
     * @param id 景点ID
     * @param userId 用户ID（可选，用于记录浏览历史）
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getAttractionDetail(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        try {
            LoggerUtil.info(logger, "处理获取景点详情请求 - ID: " + id);
            
            Attraction attraction = attractionService.getAttractionDetail(id);
            Map<String, Object> response = buildAttractionResponse(attraction);
            
            // 如果提供了用户ID，记录浏览历史
            if (userId != null && userId > 0) {
                try {
                    browsingHistoryService.recordBrowsingHistory(userId, "attraction", id);
                } catch (Exception e) {
                    LoggerUtil.warn(logger, "记录浏览历史失败: " + e.getMessage());
                    // 不影响主流程，继续返回景点详情
                }
            }
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取景点详情失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建景点端点（管理员）
     * @param request 创建请求体
     * @return API响应
     */
    @PostMapping("/admin/create")
    public ApiResponse<Map<String, Object>> createAttraction(@RequestBody CreateAttractionRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建景点请求 - 景点名称: " + request.getName());
            
            Attraction attraction = attractionService.createAttraction(
                request.getName(),
                request.getDescription(),
                request.getLocation(),
                request.getTicketPrice(),
                request.getOpeningHours(),
                request.getImageUrl(),
                request.getIsGuangzhouSpecial()
            );
            
            Map<String, Object> response = buildAttractionResponse(attraction);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建景点失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新景点端点（管理员）
     * @param id 景点ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<Map<String, Object>> updateAttraction(
            @PathVariable Long id,
            @RequestBody UpdateAttractionRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新景点请求 - ID: " + id);
            
            Attraction attraction = attractionService.updateAttraction(
                id,
                request.getName(),
                request.getDescription(),
                request.getLocation(),
                request.getTicketPrice(),
                request.getOpeningHours(),
                request.getImageUrl(),
                request.getIsGuangzhouSpecial()
            );
            
            Map<String, Object> response = buildAttractionResponse(attraction);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新景点失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除景点端点（管理员）
     * @param id 景点ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteAttraction(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除景点请求 - ID: " + id);
            
            attractionService.deleteAttraction(id);
            
            return ApiResponse.success("景点删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除景点失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 添加景点标签端点（管理员）
     * @param attractionId 景点ID
     * @param request 标签请求体
     * @return API响应
     */
    @PostMapping("/admin/{attractionId}/tags")
    public ApiResponse<Map<String, Object>> addTag(
            @PathVariable Long attractionId,
            @RequestBody AddTagRequest request) {
        try {
            LoggerUtil.info(logger, "处理添加标签请求 - 景点ID: " + attractionId + ", 标签: " + request.getTagName());
            
            AttractionTag tag = attractionService.addTag(attractionId, request.getTagName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", tag.getId());
            response.put("attractionId", tag.getAttractionId());
            response.put("tagName", tag.getTagName());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "添加标签失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除景点标签端点（管理员）
     * @param attractionId 景点ID
     * @param tagName 标签名称
     * @return API响应
     */
    @DeleteMapping("/admin/{attractionId}/tags/{tagName}")
    public ApiResponse<String> removeTag(
            @PathVariable Long attractionId,
            @PathVariable String tagName) {
        try {
            LoggerUtil.info(logger, "处理删除标签请求 - 景点ID: " + attractionId + ", 标签: " + tagName);
            
            attractionService.removeTag(attractionId, tagName);
            
            return ApiResponse.success("标签删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除标签失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建景点响应对象
     * @param attraction 景点对象
     * @return 响应Map
     */
    private Map<String, Object> buildAttractionResponse(Attraction attraction) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", attraction.getId());
        response.put("name", attraction.getName());
        response.put("description", attraction.getDescription());
        response.put("location", attraction.getLocation());
        response.put("ticketPrice", attraction.getTicketPrice());
        response.put("openingHours", attraction.getOpeningHours());
        response.put("imageUrl", attraction.getImageUrl());
        response.put("isGuangzhouSpecial", attraction.getIsGuangzhouSpecial());
        
        // 添加标签信息
        List<AttractionTag> tags = attractionService.getAttractionTags(attraction.getId());
        List<String> tagNames = tags.stream()
            .map(AttractionTag::getTagName)
            .collect(Collectors.toList());
        response.put("tags", tagNames);
        
        return response;
    }
    
    /**
     * 创建景点请求体
     */
    public static class CreateAttractionRequest {
        private String name;
        private String description;
        private String location;
        private BigDecimal ticketPrice;
        private String openingHours;
        private String imageUrl;
        private Boolean isGuangzhouSpecial;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public BigDecimal getTicketPrice() { return ticketPrice; }
        public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
        
        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public Boolean getIsGuangzhouSpecial() { return isGuangzhouSpecial; }
        public void setIsGuangzhouSpecial(Boolean isGuangzhouSpecial) { this.isGuangzhouSpecial = isGuangzhouSpecial; }
    }
    
    /**
     * 更新景点请求体
     */
    public static class UpdateAttractionRequest {
        private String name;
        private String description;
        private String location;
        private BigDecimal ticketPrice;
        private String openingHours;
        private String imageUrl;
        private Boolean isGuangzhouSpecial;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public BigDecimal getTicketPrice() { return ticketPrice; }
        public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
        
        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public Boolean getIsGuangzhouSpecial() { return isGuangzhouSpecial; }
        public void setIsGuangzhouSpecial(Boolean isGuangzhouSpecial) { this.isGuangzhouSpecial = isGuangzhouSpecial; }
    }
    
    /**
     * 添加标签请求体
     */
    public static class AddTagRequest {
        private String tagName;
        
        public String getTagName() { return tagName; }
        public void setTagName(String tagName) { this.tagName = tagName; }
    }
}
