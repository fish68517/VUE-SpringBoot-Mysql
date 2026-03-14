package com.admin.mapper;

import com.admin.entity.FitnessPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FitnessPlanMapper {
    
    /**
     * Find fitness plan by id
     */
    FitnessPlan findById(@Param("id") Long id);
    
    /**
     * Find all fitness plans with pagination
     */
    List<FitnessPlan> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * Count all fitness plans
     */
    int countAll();
    
    /**
     * Insert fitness plan
     */
    int insert(FitnessPlan fitnessPlan);
    
    /**
     * Update fitness plan
     */
    int update(FitnessPlan fitnessPlan);
    
    /**
     * Delete fitness plan by id
     */
    int deleteById(@Param("id") Long id);
}
