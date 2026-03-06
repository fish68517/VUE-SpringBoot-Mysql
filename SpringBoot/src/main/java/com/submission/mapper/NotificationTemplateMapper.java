package com.submission.mapper;

import com.submission.entity.NotificationTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * NotificationTemplate Mapper - Data access layer for NotificationTemplate entity
 */
@Mapper
public interface NotificationTemplateMapper {

    /**
     * Insert a new notification template
     */
    @Insert("INSERT INTO notification_templates (template_name, template_content, template_type, created_at, updated_at) " +
            "VALUES (#{templateName}, #{templateContent}, #{templateType}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NotificationTemplate template);

    /**
     * Find template by id
     */
    @Select("SELECT id, template_name, template_content, template_type, created_at, updated_at " +
            "FROM notification_templates WHERE id = #{id}")
    NotificationTemplate findById(Long id);

    /**
     * Find template by name
     */
    @Select("SELECT id, template_name, template_content, template_type, created_at, updated_at " +
            "FROM notification_templates WHERE template_name = #{templateName}")
    NotificationTemplate findByName(String templateName);

    /**
     * Find all templates
     */
    @Select("SELECT id, template_name, template_content, template_type, created_at, updated_at " +
            "FROM notification_templates ORDER BY created_at DESC")
    List<NotificationTemplate> findAll();

    /**
     * Find templates by type
     */
    @Select("SELECT id, template_name, template_content, template_type, created_at, updated_at " +
            "FROM notification_templates WHERE template_type = #{templateType} ORDER BY created_at DESC")
    List<NotificationTemplate> findByType(String templateType);

    /**
     * Update template
     */
    @Update("UPDATE notification_templates SET template_name = #{templateName}, " +
            "template_content = #{templateContent}, template_type = #{templateType}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(NotificationTemplate template);

    /**
     * Delete template by id
     */
    @Delete("DELETE FROM notification_templates WHERE id = #{id}")
    int deleteById(Long id);
}
