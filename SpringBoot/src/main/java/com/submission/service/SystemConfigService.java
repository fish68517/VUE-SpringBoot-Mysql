package com.submission.service;

import com.submission.dto.SystemConfigDTO;
import com.submission.dto.NotificationTemplateDTO;
import com.submission.entity.SystemConfig;
import com.submission.entity.NotificationTemplate;
import com.submission.mapper.SystemConfigMapper;
import com.submission.mapper.NotificationTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemConfig Service - Business logic for system configuration operations
 */
@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    private final NotificationTemplateMapper notificationTemplateMapper;

    /**
     * Get all system configurations
     */
    public List<SystemConfigDTO> getAllConfigs() {
        List<SystemConfig> configs = systemConfigMapper.findAll();
        return configs.stream()
                .map(this::convertConfigToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get system configuration by key
     */
    public SystemConfigDTO getConfigByKey(String configKey) {
        SystemConfig config = systemConfigMapper.findByKey(configKey);
        if (config == null) {
            throw new RuntimeException("Configuration not found: " + configKey);
        }
        return convertConfigToDTO(config);
    }

    /**
     * Get system configuration by id
     */
    public SystemConfigDTO getConfigById(Long id) {
        SystemConfig config = systemConfigMapper.findById(id);
        if (config == null) {
            throw new RuntimeException("Configuration not found");
        }
        return convertConfigToDTO(config);
    }

    /**
     * Create new system configuration
     */
    public SystemConfigDTO createConfig(SystemConfigDTO configDTO) {
        // Check if config key already exists
        SystemConfig existing = systemConfigMapper.findByKey(configDTO.getConfigKey());
        if (existing != null) {
            throw new RuntimeException("Configuration key already exists: " + configDTO.getConfigKey());
        }

        SystemConfig config = SystemConfig.builder()
                .configKey(configDTO.getConfigKey())
                .configValue(configDTO.getConfigValue())
                .description(configDTO.getDescription())
                .build();

        systemConfigMapper.insert(config);
        return convertConfigToDTO(config);
    }

    /**
     * Update system configuration by id
     */
    public SystemConfigDTO updateConfig(Long id, SystemConfigDTO configDTO) {
        SystemConfig config = systemConfigMapper.findById(id);
        if (config == null) {
            throw new RuntimeException("Configuration not found");
        }

        config.setConfigValue(configDTO.getConfigValue());
        config.setDescription(configDTO.getDescription());

        systemConfigMapper.update(config);
        return convertConfigToDTO(config);
    }

    /**
     * Update system configuration by key
     */
    public SystemConfigDTO updateConfigByKey(String configKey, SystemConfigDTO configDTO) {
        SystemConfig config = systemConfigMapper.findByKey(configKey);
        if (config == null) {
            throw new RuntimeException("Configuration not found: " + configKey);
        }

        config.setConfigValue(configDTO.getConfigValue());
        config.setDescription(configDTO.getDescription());

        systemConfigMapper.updateByKey(config);
        return convertConfigToDTO(config);
    }

    /**
     * Delete system configuration by id
     */
    public void deleteConfig(Long id) {
        SystemConfig config = systemConfigMapper.findById(id);
        if (config == null) {
            throw new RuntimeException("Configuration not found");
        }
        systemConfigMapper.deleteById(id);
    }

    /**
     * Get all notification templates
     */
    public List<NotificationTemplateDTO> getAllTemplates() {
        List<NotificationTemplate> templates = notificationTemplateMapper.findAll();
        return templates.stream()
                .map(this::convertTemplateToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notification templates by type
     */
    public List<NotificationTemplateDTO> getTemplatesByType(String templateType) {
        List<NotificationTemplate> templates = notificationTemplateMapper.findByType(templateType);
        return templates.stream()
                .map(this::convertTemplateToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notification template by id
     */
    public NotificationTemplateDTO getTemplateById(Long id) {
        NotificationTemplate template = notificationTemplateMapper.findById(id);
        if (template == null) {
            throw new RuntimeException("Notification template not found");
        }
        return convertTemplateToDTO(template);
    }

    /**
     * Get notification template by name
     */
    public NotificationTemplateDTO getTemplateByName(String templateName) {
        NotificationTemplate template = notificationTemplateMapper.findByName(templateName);
        if (template == null) {
            throw new RuntimeException("Notification template not found: " + templateName);
        }
        return convertTemplateToDTO(template);
    }

    /**
     * Create new notification template
     */
    public NotificationTemplateDTO createTemplate(NotificationTemplateDTO templateDTO) {
        // Check if template name already exists
        NotificationTemplate existing = notificationTemplateMapper.findByName(templateDTO.getTemplateName());
        if (existing != null) {
            throw new RuntimeException("Template name already exists: " + templateDTO.getTemplateName());
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .templateName(templateDTO.getTemplateName())
                .templateContent(templateDTO.getTemplateContent())
                .templateType(templateDTO.getTemplateType())
                .build();

        notificationTemplateMapper.insert(template);
        return convertTemplateToDTO(template);
    }

    /**
     * Update notification template
     */
    public NotificationTemplateDTO updateTemplate(Long id, NotificationTemplateDTO templateDTO) {
        NotificationTemplate template = notificationTemplateMapper.findById(id);
        if (template == null) {
            throw new RuntimeException("Notification template not found");
        }

        template.setTemplateName(templateDTO.getTemplateName());
        template.setTemplateContent(templateDTO.getTemplateContent());
        template.setTemplateType(templateDTO.getTemplateType());

        notificationTemplateMapper.update(template);
        return convertTemplateToDTO(template);
    }

    /**
     * Delete notification template
     */
    public void deleteTemplate(Long id) {
        NotificationTemplate template = notificationTemplateMapper.findById(id);
        if (template == null) {
            throw new RuntimeException("Notification template not found");
        }
        notificationTemplateMapper.deleteById(id);
    }

    /**
     * Convert SystemConfig entity to DTO
     */
    private SystemConfigDTO convertConfigToDTO(SystemConfig config) {
        return SystemConfigDTO.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .build();
    }

    /**
     * Convert NotificationTemplate entity to DTO
     */
    private NotificationTemplateDTO convertTemplateToDTO(NotificationTemplate template) {
        return NotificationTemplateDTO.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .templateContent(template.getTemplateContent())
                .templateType(template.getTemplateType())
                .build();
    }
}
