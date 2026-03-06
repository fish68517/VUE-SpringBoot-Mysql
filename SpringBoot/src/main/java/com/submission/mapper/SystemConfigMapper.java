package com.submission.mapper;

import com.submission.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * SystemConfig Mapper - Data access layer for SystemConfig entity
 */
@Mapper
public interface SystemConfigMapper {

    /**
     * Insert a new system config
     */
    @Insert("INSERT INTO system_config (config_key, config_value, description, created_at, updated_at) " +
            "VALUES (#{configKey}, #{configValue}, #{description}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfig config);

    /**
     * Find config by id
     */
    @Select("SELECT id, config_key, config_value, description, created_at, updated_at " +
            "FROM system_config WHERE id = #{id}")
    SystemConfig findById(Long id);

    /**
     * Find config by key
     */
    @Select("SELECT id, config_key, config_value, description, created_at, updated_at " +
            "FROM system_config WHERE config_key = #{configKey}")
    SystemConfig findByKey(String configKey);

    /**
     * Find all configs
     */
    @Select("SELECT id, config_key, config_value, description, created_at, updated_at " +
            "FROM system_config ORDER BY created_at DESC")
    List<SystemConfig> findAll();

    /**
     * Update config
     */
    @Update("UPDATE system_config SET config_value = #{configValue}, description = #{description}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int update(SystemConfig config);

    /**
     * Update config by key
     */
    @Update("UPDATE system_config SET config_value = #{configValue}, description = #{description}, " +
            "updated_at = NOW() WHERE config_key = #{configKey}")
    int updateByKey(SystemConfig config);

    /**
     * Delete config by id
     */
    @Delete("DELETE FROM system_config WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * Delete config by key
     */
    @Delete("DELETE FROM system_config WHERE config_key = #{configKey}")
    int deleteByKey(String configKey);
}
