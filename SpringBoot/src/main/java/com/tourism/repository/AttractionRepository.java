package com.tourism.repository;

import com.tourism.entity.Attraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 景点数据访问接口
 */
@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    
    /**
     * 根据景点名称查询景点
     * @param name 景点名称
     * @return 景点对象
     */
    Optional<Attraction> findByName(String name);
    
    /**
     * 查询所有广州特色景点
     * @return 广州特色景点列表
     */
    List<Attraction> findByIsGuangzhouSpecialTrue();
    
    /**
     * 根据位置查询景点
     * @param location 位置
     * @return 景点列表
     */
    List<Attraction> findByLocation(String location);
    
    /**
     * 检查景点名称是否存在
     * @param name 景点名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 按关键词搜索景点（分页）
     * @param keyword 关键词
     * @param pageable 分页信息
     * @return 景点分页数据
     */
    @Query("SELECT a FROM Attraction a WHERE a.name LIKE %:keyword% OR a.description LIKE %:keyword%")
    Page<Attraction> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 查询广州特色景点（分页）
     * @param pageable 分页信息
     * @return 景点分页数据
     */
    Page<Attraction> findByIsGuangzhouSpecialTrue(Pageable pageable);
    
    /**
     * 按标签查询景点（分页）
     * @param tagName 标签名称
     * @param pageable 分页信息
     * @return 景点分页数据
     */
    @Query("SELECT DISTINCT a FROM Attraction a JOIN AttractionTag t ON a.id = t.attractionId WHERE t.tagName = :tagName")
    Page<Attraction> findByTagName(@Param("tagName") String tagName, Pageable pageable);
}
