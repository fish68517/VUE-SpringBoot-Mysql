package com.agricultural.repository;

import com.agricultural.entity.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 作物数据访问层接口
 */
@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    
    /**
     * 根据用户ID查询作物列表
     */
    List<Crop> findByUserId(Long userId);
    
    /**
     * 根据地区查询作物列表
     */
    List<Crop> findByRegion(String region);
    
    /**
     * 根据作物名称查询作物列表
     */
    List<Crop> findByCropName(String cropName);
    
    /**
     * 根据生育期查询作物列表
     */
    List<Crop> findByGrowthStage(String growthStage);
    
    /**
     * 根据用户ID和地区查询作物列表
     */
    List<Crop> findByUserIdAndRegion(Long userId, String region);
}
