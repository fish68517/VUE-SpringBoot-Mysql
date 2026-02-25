package com.tourism.repository;

import com.tourism.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 旅游路线数据访问接口
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    /**
     * 根据路线名称查询路线
     * @param name 路线名称
     * @return 路线对象
     */
    Optional<Route> findByName(String name);
    
    /**
     * 检查路线名称是否存在
     * @param name 路线名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 按关键词搜索路线（分页）
     * @param keyword 关键词
     * @param pageable 分页信息
     * @return 路线分页数据
     */
    @Query("SELECT r FROM Route r WHERE r.name LIKE %:keyword% OR r.description LIKE %:keyword%")
    Page<Route> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 按天数查询路线（分页）
     * @param durationDays 天数
     * @param pageable 分页信息
     * @return 路线分页数据
     */
    Page<Route> findByDurationDays(Integer durationDays, Pageable pageable);
    
    /**
     * 查询所有路线（分页）
     * @param pageable 分页信息
     * @return 路线分页数据
     */
    Page<Route> findAll(Pageable pageable);
}
