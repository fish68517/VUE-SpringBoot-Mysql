package com.tourism.service;

import com.tourism.entity.Route;
import com.tourism.entity.RouteItem;
import com.tourism.entity.Attraction;
import com.tourism.exception.BusinessException;
import com.tourism.repository.RouteRepository;
import com.tourism.repository.RouteItemRepository;
import com.tourism.repository.AttractionRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * 旅游路线业务逻辑服务
 */
@Service
public class RouteService {
    
    private static final Logger logger = LoggerUtil.getLogger(RouteService.class);
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private RouteItemRepository routeItemRepository;
    
    @Autowired
    private AttractionRepository attractionRepository;
    
    /**
     * 获取路线列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 路线分页数据
     */
    public Page<Route> getRouteList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Route> routes = routeRepository.findAll(pageable);
        LoggerUtil.info(logger, "获取路线列表成功 - 页码: " + page + ", 每页数量: " + size);
        return routes;
    }
    
    /**
     * 根据关键词搜索路线（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 路线分页数据
     */
    public Page<Route> searchRoutes(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Route> routes = routeRepository.searchByKeyword(keyword, pageable);
        
        LoggerUtil.info(logger, "搜索路线成功 - 关键词: " + keyword + ", 页码: " + page);
        return routes;
    }
    
    /**
     * 根据天数查询路线（分页）
     * @param durationDays 天数
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 路线分页数据
     */
    public Page<Route> getRoutesByDuration(Integer durationDays, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Route> routes = routeRepository.findByDurationDays(durationDays, pageable);
        
        LoggerUtil.info(logger, "按天数查询路线成功 - 天数: " + durationDays + ", 页码: " + page);
        return routes;
    }
    
    /**
     * 根据ID查询路线详情
     * @param id 路线ID
     * @return 路线对象
     */
    public Route getRouteDetail(Long id) {
        Optional<Route> routeOptional = routeRepository.findById(id);
        if (!routeOptional.isPresent()) {
            LoggerUtil.warn(logger, "获取路线详情失败：路线不存在 - ID: " + id);
            throw new BusinessException("路线不存在");
        }
        
        LoggerUtil.info(logger, "获取路线详情成功 - ID: " + id);
        return routeOptional.get();
    }
    
    /**
     * 获取路线中的所有项目
     * @param routeId 路线ID
     * @return 路线项目列表
     */
    public List<RouteItem> getRouteItems(Long routeId) {
        List<RouteItem> items = routeItemRepository.findByRouteId(routeId);
        LoggerUtil.info(logger, "获取路线项目成功 - 路线ID: " + routeId + ", 项目数: " + items.size());
        return items;
    }
    
    /**
     * 创建路线（管理员功能）
     * @param name 路线名称
     * @param description 路线描述
     * @param durationDays 天数
     * @param totalPrice 总价格
     * @return 创建的路线对象
     */
    public Route createRoute(String name, String description, Integer durationDays, BigDecimal totalPrice) {
        // 检查路线名称是否已存在
        if (routeRepository.existsByName(name)) {
            LoggerUtil.warn(logger, "创建路线失败：路线名称已存在 - " + name);
            throw new BusinessException("路线名称已存在");
        }
        
        // 验证天数
        if (durationDays == null || durationDays <= 0) {
            LoggerUtil.warn(logger, "创建路线失败：天数无效 - " + durationDays);
            throw new BusinessException("天数必须大于0");
        }
        
        // 验证价格
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            LoggerUtil.warn(logger, "创建路线失败：价格无效 - " + totalPrice);
            throw new BusinessException("价格不能为负数");
        }
        
        // 创建新路线
        Route route = new Route();
        route.setName(name);
        route.setDescription(description);
        route.setDurationDays(durationDays);
        route.setTotalPrice(totalPrice);
        
        // 保存路线
        Route savedRoute = routeRepository.save(route);
        LoggerUtil.info(logger, "创建路线成功 - 路线名称: " + name);
        
        return savedRoute;
    }
    
    /**
     * 更新路线信息（管理员功能）
     * @param id 路线ID
     * @param name 路线名称
     * @param description 路线描述
     * @param durationDays 天数
     * @param totalPrice 总价格
     * @return 更新后的路线对象
     */
    public Route updateRoute(Long id, String name, String description, Integer durationDays, BigDecimal totalPrice) {
        Optional<Route> routeOptional = routeRepository.findById(id);
        if (!routeOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新路线失败：路线不存在 - ID: " + id);
            throw new BusinessException("路线不存在");
        }
        
        Route route = routeOptional.get();
        
        // 检查新名称是否已被其他路线使用
        if (name != null && !name.isEmpty() && !name.equals(route.getName())) {
            if (routeRepository.existsByName(name)) {
                LoggerUtil.warn(logger, "更新路线失败：路线名称已存在 - " + name);
                throw new BusinessException("路线名称已存在");
            }
        }
        
        // 更新路线信息
        if (name != null && !name.isEmpty()) {
            route.setName(name);
        }
        if (description != null) {
            route.setDescription(description);
        }
        if (durationDays != null && durationDays > 0) {
            route.setDurationDays(durationDays);
        }
        if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) >= 0) {
            route.setTotalPrice(totalPrice);
        }
        
        Route updatedRoute = routeRepository.save(route);
        LoggerUtil.info(logger, "更新路线成功 - ID: " + id);
        
        return updatedRoute;
    }
    
    /**
     * 删除路线（管理员功能）
     * @param id 路线ID
     */
    public void deleteRoute(Long id) {
        Optional<Route> routeOptional = routeRepository.findById(id);
        if (!routeOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除路线失败：路线不存在 - ID: " + id);
            throw new BusinessException("路线不存在");
        }
        
        Route route = routeOptional.get();
        
        // 删除路线的所有项目
        routeItemRepository.deleteByRouteId(id);
        
        // 删除路线
        routeRepository.deleteById(id);
        LoggerUtil.info(logger, "删除路线成功 - ID: " + id + ", 路线名称: " + route.getName());
    }
    
    /**
     * 添加路线项目（景点或酒店）
     * @param routeId 路线ID
     * @param dayNumber 第几天
     * @param itemType 项目类型（'attraction' 或 'hotel'）
     * @param itemId 项目ID
     * @param sequence 顺序
     * @return 创建的路线项目对象
     */
    public RouteItem addRouteItem(Long routeId, Integer dayNumber, String itemType, Long itemId, Integer sequence) {
        // 检查路线是否存在
        if (!routeRepository.existsById(routeId)) {
            LoggerUtil.warn(logger, "添加路线项目失败：路线不存在 - ID: " + routeId);
            throw new BusinessException("路线不存在");
        }
        
        // 验证项目类型
        if (!itemType.equals("attraction") && !itemType.equals("hotel")) {
            LoggerUtil.warn(logger, "添加路线项目失败：项目类型无效 - " + itemType);
            throw new BusinessException("项目类型必须为'attraction'或'hotel'");
        }
        
        // 验证天数
        if (dayNumber == null || dayNumber <= 0) {
            LoggerUtil.warn(logger, "添加路线项目失败：天数无效 - " + dayNumber);
            throw new BusinessException("天数必须大于0");
        }
        
        // 创建新路线项目
        RouteItem routeItem = new RouteItem();
        routeItem.setRouteId(routeId);
        routeItem.setDayNumber(dayNumber);
        routeItem.setItemType(itemType);
        routeItem.setItemId(itemId);
        routeItem.setSequence(sequence != null ? sequence : 0);
        
        RouteItem savedItem = routeItemRepository.save(routeItem);
        LoggerUtil.info(logger, "添加路线项目成功 - 路线ID: " + routeId + ", 项目类型: " + itemType + ", 项目ID: " + itemId);
        
        return savedItem;
    }
    
    /**
     * 删除路线项目
     * @param routeItemId 路线项目ID
     */
    public void removeRouteItem(Long routeItemId) {
        if (!routeItemRepository.existsById(routeItemId)) {
            LoggerUtil.warn(logger, "删除路线项目失败：项目不存在 - ID: " + routeItemId);
            throw new BusinessException("路线项目不存在");
        }
        
        routeItemRepository.deleteById(routeItemId);
        LoggerUtil.info(logger, "删除路线项目成功 - ID: " + routeItemId);
    }
    
    /**
     * 获取路线中特定天数的项目
     * @param routeId 路线ID
     * @param dayNumber 天数
     * @return 路线项目列表
     */
    public List<RouteItem> getRouteItemsByDay(Long routeId, Integer dayNumber) {
        List<RouteItem> items = routeItemRepository.findByRouteIdAndDayNumber(routeId, dayNumber);
        LoggerUtil.info(logger, "获取路线特定天数项目成功 - 路线ID: " + routeId + ", 天数: " + dayNumber + ", 项目数: " + items.size());
        return items;
    }
    
    /**
     * 根据时间和景点偏好推荐路线
     * @param durationDays 旅游天数
     * @param preferredAttractionIds 偏好的景点ID列表（可为空）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 推荐路线分页数据
     */
    public Page<Route> recommendRoutes(Integer durationDays, List<Long> preferredAttractionIds, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 首先按天数查询匹配的路线
        Page<Route> matchedRoutes = routeRepository.findByDurationDays(durationDays, pageable);
        
        LoggerUtil.info(logger, "推荐路线成功 - 天数: " + durationDays + ", 页码: " + page + ", 匹配路线数: " + matchedRoutes.getTotalElements());
        return matchedRoutes;
    }
    
    /**
     * 获取包含广州特色景点的路线
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 包含广州特色景点的路线分页数据
     */
    public Page<Route> getGuangzhouSpecialRoutes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 获取所有路线
        Page<Route> allRoutes = routeRepository.findAll(pageable);
        
        // 获取所有广州特色景点
        List<Attraction> guangzhouSpecialAttractions = attractionRepository.findByIsGuangzhouSpecialTrue();
        Set<Long> guangzhouAttractionIds = guangzhouSpecialAttractions.stream()
            .map(Attraction::getId)
            .collect(Collectors.toSet());
        
        // 过滤包含广州特色景点的路线
        List<Route> filteredRoutes = allRoutes.getContent().stream()
            .filter(route -> {
                List<RouteItem> items = routeItemRepository.findByRouteId(route.getId());
                return items.stream()
                    .anyMatch(item -> "attraction".equals(item.getItemType()) && 
                                     guangzhouAttractionIds.contains(item.getItemId()));
            })
            .collect(Collectors.toList());
        
        LoggerUtil.info(logger, "获取广州特色路线成功 - 页码: " + page + ", 包含广州特色景点的路线数: " + filteredRoutes.size());
        
        // 返回过滤后的结果（注意：这里返回的是内存分页，实际应用中应该优化为数据库查询）
        return new org.springframework.data.domain.PageImpl<>(filteredRoutes, pageable, filteredRoutes.size());
    }
    
    /**
     * 根据景点偏好推荐路线
     * @param preferredAttractionIds 偏好的景点ID列表
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 推荐路线分页数据
     */
    public Page<Route> recommendRoutesByPreference(List<Long> preferredAttractionIds, int page, int size) {
        if (preferredAttractionIds == null || preferredAttractionIds.isEmpty()) {
            LoggerUtil.warn(logger, "推荐路线失败：偏好景点列表为空");
            throw new BusinessException("偏好景点列表不能为空");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        
        // 获取所有路线
        Page<Route> allRoutes = routeRepository.findAll(pageable);
        
        // 过滤包含偏好景点的路线，并按匹配度排序
        List<Route> recommendedRoutes = allRoutes.getContent().stream()
            .map(route -> {
                List<RouteItem> items = routeItemRepository.findByRouteId(route.getId());
                long matchCount = items.stream()
                    .filter(item -> "attraction".equals(item.getItemType()) && 
                                   preferredAttractionIds.contains(item.getItemId()))
                    .count();
                return new RouteWithMatchCount(route, matchCount);
            })
            .filter(routeWithMatch -> routeWithMatch.matchCount > 0)
            .sorted((a, b) -> Long.compare(b.matchCount, a.matchCount))
            .map(routeWithMatch -> routeWithMatch.route)
            .collect(Collectors.toList());
        
        LoggerUtil.info(logger, "根据景点偏好推荐路线成功 - 页码: " + page + ", 推荐路线数: " + recommendedRoutes.size());
        
        return new org.springframework.data.domain.PageImpl<>(recommendedRoutes, pageable, recommendedRoutes.size());
    }
    
    /**
     * 获取优先推荐的路线（包含广州特色景点的路线）
     * @param durationDays 旅游天数（可为空，表示不限制天数）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 优先推荐的路线分页数据
     */
    public Page<Route> getPriorityRecommendedRoutes(Integer durationDays, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        // 获取所有广州特色景点
        List<Attraction> guangzhouSpecialAttractions = attractionRepository.findByIsGuangzhouSpecialTrue();
        Set<Long> guangzhouAttractionIds = guangzhouSpecialAttractions.stream()
            .map(Attraction::getId)
            .collect(Collectors.toSet());
        
        // 获取所有路线
        Page<Route> allRoutes;
        if (durationDays != null && durationDays > 0) {
            allRoutes = routeRepository.findByDurationDays(durationDays, pageable);
        } else {
            allRoutes = routeRepository.findAll(pageable);
        }
        
        // 过滤并排序：优先推荐包含广州特色景点的路线
        List<Route> priorityRoutes = allRoutes.getContent().stream()
            .map(route -> {
                List<RouteItem> items = routeItemRepository.findByRouteId(route.getId());
                long guangzhouCount = items.stream()
                    .filter(item -> "attraction".equals(item.getItemType()) && 
                                   guangzhouAttractionIds.contains(item.getItemId()))
                    .count();
                return new RouteWithGuangzhouCount(route, guangzhouCount);
            })
            .sorted((a, b) -> Long.compare(b.guangzhouCount, a.guangzhouCount))
            .map(routeWithGuangzhou -> routeWithGuangzhou.route)
            .collect(Collectors.toList());
        
        LoggerUtil.info(logger, "获取优先推荐路线成功 - 天数: " + durationDays + ", 页码: " + page + ", 推荐路线数: " + priorityRoutes.size());
        
        return new org.springframework.data.domain.PageImpl<>(priorityRoutes, pageable, priorityRoutes.size());
    }
    
    /**
     * 内部类：用于临时存储路线和匹配计数
     */
    private static class RouteWithMatchCount {
        Route route;
        long matchCount;
        
        RouteWithMatchCount(Route route, long matchCount) {
            this.route = route;
            this.matchCount = matchCount;
        }
    }
    
    /**
     * 内部类：用于临时存储路线和广州特色景点计数
     */
    private static class RouteWithGuangzhouCount {
        Route route;
        long guangzhouCount;
        
        RouteWithGuangzhouCount(Route route, long guangzhouCount) {
            this.route = route;
            this.guangzhouCount = guangzhouCount;
        }
    }
}
