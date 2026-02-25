package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Route;
import com.tourism.entity.RouteItem;
import com.tourism.service.RouteService;
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
 * 旅游路线管理控制器
 */
@RestController
@RequestMapping("/routes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RouteController {
    
    private static final Logger logger = LoggerUtil.getLogger(RouteController.class);
    
    @Autowired
    private RouteService routeService;
    
    /**
     * 获取路线列表端点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getRouteList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取路线列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            Page<Route> routePage = routeService.getRouteList(page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取路线列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 搜索路线端点（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchRoutes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理搜索路线请求 - 关键词: " + keyword + ", 页码: " + page);
            
            Page<Route> routePage = routeService.searchRoutes(keyword, page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "搜索路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 按天数查询路线端点（分页）
     * @param durationDays 天数
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/by-duration")
    public ApiResponse<Map<String, Object>> getRoutesByDuration(
            @RequestParam Integer durationDays,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理按天数查询路线请求 - 天数: " + durationDays + ", 页码: " + page);
            
            Page<Route> routePage = routeService.getRoutesByDuration(durationDays, page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "按天数查询路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取路线详情端点
     * @param id 路线ID
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getRouteDetail(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理获取路线详情请求 - ID: " + id);
            
            Route route = routeService.getRouteDetail(id);
            Map<String, Object> response = buildRouteResponse(route);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取路线详情失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建路线端点（管理员）
     * @param request 创建请求体
     * @return API响应
     */
    @PostMapping("/admin/create")
    public ApiResponse<Map<String, Object>> createRoute(@RequestBody CreateRouteRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建路线请求 - 路线名称: " + request.getName());
            
            Route route = routeService.createRoute(
                request.getName(),
                request.getDescription(),
                request.getDurationDays(),
                request.getTotalPrice()
            );
            
            Map<String, Object> response = buildRouteResponse(route);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新路线端点（管理员）
     * @param id 路线ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<Map<String, Object>> updateRoute(
            @PathVariable Long id,
            @RequestBody UpdateRouteRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新路线请求 - ID: " + id);
            
            Route route = routeService.updateRoute(
                id,
                request.getName(),
                request.getDescription(),
                request.getDurationDays(),
                request.getTotalPrice()
            );
            
            Map<String, Object> response = buildRouteResponse(route);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除路线端点（管理员）
     * @param id 路线ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteRoute(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除路线请求 - ID: " + id);
            
            routeService.deleteRoute(id);
            
            return ApiResponse.success("路线删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 添加路线项目端点（管理员）
     * @param routeId 路线ID
     * @param request 添加项目请求体
     * @return API响应
     */
    @PostMapping("/admin/{routeId}/items")
    public ApiResponse<Map<String, Object>> addRouteItem(
            @PathVariable Long routeId,
            @RequestBody AddRouteItemRequest request) {
        try {
            LoggerUtil.info(logger, "处理添加路线项目请求 - 路线ID: " + routeId + ", 项目类型: " + request.getItemType());
            
            RouteItem routeItem = routeService.addRouteItem(
                routeId,
                request.getDayNumber(),
                request.getItemType(),
                request.getItemId(),
                request.getSequence()
            );
            
            Map<String, Object> response = buildRouteItemResponse(routeItem);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "添加路线项目失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除路线项目端点（管理员）
     * @param routeItemId 路线项目ID
     * @return API响应
     */
    @DeleteMapping("/admin/items/{routeItemId}")
    public ApiResponse<String> removeRouteItem(@PathVariable Long routeItemId) {
        try {
            LoggerUtil.info(logger, "处理删除路线项目请求 - ID: " + routeItemId);
            
            routeService.removeRouteItem(routeItemId);
            
            return ApiResponse.success("路线项目删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除路线项目失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取路线项目端点
     * @param routeId 路线ID
     * @return API响应
     */
    @GetMapping("/{routeId}/items")
    public ApiResponse<Map<String, Object>> getRouteItems(@PathVariable Long routeId) {
        try {
            LoggerUtil.info(logger, "处理获取路线项目请求 - 路线ID: " + routeId);
            
            List<RouteItem> items = routeService.getRouteItems(routeId);
            
            List<Map<String, Object>> itemResponses = items.stream()
                .map(this::buildRouteItemResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("items", itemResponses);
            response.put("total", items.size());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取路线项目失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取路线特定天数的项目端点
     * @param routeId 路线ID
     * @param dayNumber 天数
     * @return API响应
     */
    @GetMapping("/{routeId}/items/day/{dayNumber}")
    public ApiResponse<Map<String, Object>> getRouteItemsByDay(
            @PathVariable Long routeId,
            @PathVariable Integer dayNumber) {
        try {
            LoggerUtil.info(logger, "处理获取路线特定天数项目请求 - 路线ID: " + routeId + ", 天数: " + dayNumber);
            
            List<RouteItem> items = routeService.getRouteItemsByDay(routeId, dayNumber);
            
            List<Map<String, Object>> itemResponses = items.stream()
                .map(this::buildRouteItemResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("items", itemResponses);
            response.put("total", items.size());
            response.put("dayNumber", dayNumber);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取路线特定天数项目失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 推荐路线端点（基于时间和景点偏好）
     * @param durationDays 旅游天数
     * @param preferredAttractionIds 偏好的景点ID列表（可选）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/recommend/by-duration")
    public ApiResponse<Map<String, Object>> recommendRoutes(
            @RequestParam Integer durationDays,
            @RequestParam(required = false) List<Long> preferredAttractionIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理推荐路线请求 - 天数: " + durationDays + ", 页码: " + page);
            
            Page<Route> routePage = routeService.recommendRoutes(durationDays, preferredAttractionIds, page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            response.put("durationDays", durationDays);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "推荐路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取广州特色路线端点
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/recommend/guangzhou-special")
    public ApiResponse<Map<String, Object>> getGuangzhouSpecialRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取广州特色路线请求 - 页码: " + page);
            
            Page<Route> routePage = routeService.getGuangzhouSpecialRoutes(page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取广州特色路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据景点偏好推荐路线端点
     * @param preferredAttractionIds 偏好的景点ID列表
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @PostMapping("/recommend/by-preference")
    public ApiResponse<Map<String, Object>> recommendRoutesByPreference(
            @RequestBody RecommendByPreferenceRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理根据景点偏好推荐路线请求 - 页码: " + page);
            
            Page<Route> routePage = routeService.recommendRoutesByPreference(request.getPreferredAttractionIds(), page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "根据景点偏好推荐路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取优先推荐的路线端点（包含广州特色景点的路线）
     * @param durationDays 旅游天数（可选）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/recommend/priority")
    public ApiResponse<Map<String, Object>> getPriorityRecommendedRoutes(
            @RequestParam(required = false) Integer durationDays,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取优先推荐路线请求 - 天数: " + durationDays + ", 页码: " + page);
            
            Page<Route> routePage = routeService.getPriorityRecommendedRoutes(durationDays, page, size);
            
            // 为每个路线添加项目信息
            List<Map<String, Object>> routes = routePage.getContent().stream()
                .map(this::buildRouteResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("routes", routes);
            response.put("total", routePage.getTotalElements());
            response.put("currentPage", routePage.getNumber());
            response.put("pageSize", routePage.getSize());
            response.put("totalPages", routePage.getTotalPages());
            if (durationDays != null) {
                response.put("durationDays", durationDays);
            }
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取优先推荐路线失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建路线响应对象
     * @param route 路线对象
     * @return 响应Map
     */
    private Map<String, Object> buildRouteResponse(Route route) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", route.getId());
        response.put("name", route.getName());
        response.put("description", route.getDescription());
        response.put("durationDays", route.getDurationDays());
        response.put("totalPrice", route.getTotalPrice());
        response.put("createdAt", route.getCreatedAt());
        response.put("updatedAt", route.getUpdatedAt());
        
        // 添加路线项目信息
        List<RouteItem> items = routeService.getRouteItems(route.getId());
        List<Map<String, Object>> itemResponses = items.stream()
            .map(this::buildRouteItemResponse)
            .collect(Collectors.toList());
        response.put("items", itemResponses);
        
        return response;
    }
    
    /**
     * 构建路线项目响应对象
     * @param routeItem 路线项目对象
     * @return 响应Map
     */
    private Map<String, Object> buildRouteItemResponse(RouteItem routeItem) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", routeItem.getId());
        response.put("routeId", routeItem.getRouteId());
        response.put("dayNumber", routeItem.getDayNumber());
        response.put("itemType", routeItem.getItemType());
        response.put("itemId", routeItem.getItemId());
        response.put("sequence", routeItem.getSequence());
        response.put("createdAt", routeItem.getCreatedAt());
        
        return response;
    }
    
    /**
     * 创建路线请求体
     */
    public static class CreateRouteRequest {
        private String name;
        private String description;
        private Integer durationDays;
        private BigDecimal totalPrice;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getDurationDays() { return durationDays; }
        public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
        
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }
    
    /**
     * 更新路线请求体
     */
    public static class UpdateRouteRequest {
        private String name;
        private String description;
        private Integer durationDays;
        private BigDecimal totalPrice;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getDurationDays() { return durationDays; }
        public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
        
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }
    
    /**
     * 添加路线项目请求体
     */
    public static class AddRouteItemRequest {
        private Integer dayNumber;
        private String itemType;
        private Long itemId;
        private Integer sequence;
        
        public Integer getDayNumber() { return dayNumber; }
        public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }
        
        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }
        
        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }
        
        public Integer getSequence() { return sequence; }
        public void setSequence(Integer sequence) { this.sequence = sequence; }
    }
    
    /**
     * 根据景点偏好推荐路线请求体
     */
    public static class RecommendByPreferenceRequest {
        private List<Long> preferredAttractionIds;
        
        public List<Long> getPreferredAttractionIds() { return preferredAttractionIds; }
        public void setPreferredAttractionIds(List<Long> preferredAttractionIds) { this.preferredAttractionIds = preferredAttractionIds; }
    }
}
