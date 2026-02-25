package com.tourism.repository;

import com.tourism.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
