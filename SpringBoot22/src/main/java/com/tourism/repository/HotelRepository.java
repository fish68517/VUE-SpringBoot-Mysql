package com.tourism.repository;

import com.tourism.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 酒店数据访问接口
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    
    /**
     * 根据酒店名称查询酒店
     * @param name 酒店名称
     * @return 酒店对象
     */
    Optional<Hotel> findByName(String name);
    
    /**
     * 根据位置查询酒店
     * @param location 位置
     * @return 酒店列表
     */
    List<Hotel> findByLocation(String location);
    
    /**
     * 检查酒店名称是否存在
     * @param name 酒店名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 按关键词搜索酒店（分页）
     * @param keyword 关键词
     * @param pageable 分页信息
     * @return 酒店分页数据
     */
    @Query("SELECT h FROM Hotel h WHERE h.name LIKE %:keyword% OR h.description LIKE %:keyword% OR h.location LIKE %:keyword%")
    Page<Hotel> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 按位置查询酒店（分页）
     * @param location 位置
     * @param pageable 分页信息
     * @return 酒店分页数据
     */
    Page<Hotel> findByLocation(String location, Pageable pageable);
    
    /**
     * 查询所有酒店（分页）
     * @param pageable 分页信息
     * @return 酒店分页数据
     */
    Page<Hotel> findAll(Pageable pageable);
}
