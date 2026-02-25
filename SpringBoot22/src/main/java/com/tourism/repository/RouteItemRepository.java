package com.tourism.repository;

import com.tourism.entity.RouteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 旅游路线项目数据访问接口
 */
@Repository
public interface RouteItemRepository extends JpaRepository<RouteItem, Long> {
    
    /**
     * 根据路线ID查询所有路线项目
     * @param routeId 路线ID
     * @return 路线项目列表
     */
    List<RouteItem> findByRouteId(Long routeId);
    
    /**
     * 根据路线ID和天数查询路线项目
     * @param routeId 路线ID
     * @param dayNumber 天数
     * @return 路线项目列表
     */
    List<RouteItem> findByRouteIdAndDayNumber(Long routeId, Integer dayNumber);
    
    /**
     * 根据路线ID和项目类型查询路线项目
     * @param routeId 路线ID
     * @param itemType 项目类型（'attraction' 或 'hotel'）
     * @return 路线项目列表
     */
    List<RouteItem> findByRouteIdAndItemType(Long routeId, String itemType);
    
    /**
     * 删除路线的所有项目
     * @param routeId 路线ID
     */
    void deleteByRouteId(Long routeId);
    
    /**
     * 查询路线中包含特定景点的项目
     * @param routeId 路线ID
     * @param itemId 项目ID
     * @param itemType 项目类型
     * @return 路线项目列表
     */
    @Query("SELECT ri FROM RouteItem ri WHERE ri.routeId = :routeId AND ri.itemId = :itemId AND ri.itemType = :itemType")
    List<RouteItem> findByRouteIdAndItemIdAndItemType(@Param("routeId") Long routeId, @Param("itemId") Long itemId, @Param("itemType") String itemType);
}
