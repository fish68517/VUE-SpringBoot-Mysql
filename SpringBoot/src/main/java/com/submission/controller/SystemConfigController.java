package com.submission.controller;

import com.submission.dto.SystemConfigDTO;
import com.submission.dto.NotificationTemplateDTO;
import com.submission.service.SystemConfigService;
import com.submission.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * SystemConfig Controller - Handles system configuration operations
 */
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    /**
     * Get all system configurations
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemConfigDTO>>> getAllConfigs(HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<SystemConfigDTO> configs = systemConfigService.getAllConfigs();
            return ResponseEntity.ok(ApiResponse.success("System configurations retrieved", configs));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get system configuration by key
     */
    @GetMapping("/key/{configKey}")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> getConfigByKey(
            @PathVariable String configKey,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            SystemConfigDTO config = systemConfigService.getConfigByKey(configKey);
            return ResponseEntity.ok(ApiResponse.success("System configuration retrieved", config));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get system configuration by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> getConfigById(
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            SystemConfigDTO config = systemConfigService.getConfigById(id);
            return ResponseEntity.ok(ApiResponse.success("System configuration retrieved", config));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Create new system configuration
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SystemConfigDTO>> createConfig(
            @RequestBody SystemConfigDTO configDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            SystemConfigDTO createdConfig = systemConfigService.createConfig(configDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("System configuration created", createdConfig));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update system configuration by id
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> updateConfig(
            @PathVariable Long id,
            @RequestBody SystemConfigDTO configDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            SystemConfigDTO updatedConfig = systemConfigService.updateConfig(id, configDTO);
            return ResponseEntity.ok(ApiResponse.success("System configuration updated", updatedConfig));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update system configuration by key
     */
    @PutMapping("/key/{configKey}")
    public ResponseEntity<ApiResponse<SystemConfigDTO>> updateConfigByKey(
            @PathVariable String configKey,
            @RequestBody SystemConfigDTO configDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            SystemConfigDTO updatedConfig = systemConfigService.updateConfigByKey(configKey, configDTO);
            return ResponseEntity.ok(ApiResponse.success("System configuration updated", updatedConfig));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete system configuration by id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConfig(
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            systemConfigService.deleteConfig(id);
            return ResponseEntity.ok(ApiResponse.success("System configuration deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get all notification templates
     */
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<NotificationTemplateDTO>>> getAllTemplates(HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<NotificationTemplateDTO> templates = systemConfigService.getAllTemplates();
            return ResponseEntity.ok(ApiResponse.success("Notification templates retrieved", templates));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get notification templates by type
     */
    @GetMapping("/templates/type/{templateType}")
    public ResponseEntity<ApiResponse<List<NotificationTemplateDTO>>> getTemplatesByType(
            @PathVariable String templateType,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            List<NotificationTemplateDTO> templates = systemConfigService.getTemplatesByType(templateType);
            return ResponseEntity.ok(ApiResponse.success("Notification templates retrieved", templates));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Get notification template by id
     */
    @GetMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> getTemplateById(
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            NotificationTemplateDTO template = systemConfigService.getTemplateById(id);
            return ResponseEntity.ok(ApiResponse.success("Notification template retrieved", template));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Create new notification template
     */
    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> createTemplate(
            @RequestBody NotificationTemplateDTO templateDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            NotificationTemplateDTO createdTemplate = systemConfigService.createTemplate(templateDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Notification template created", createdTemplate));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Update notification template
     */
    @PutMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @RequestBody NotificationTemplateDTO templateDTO,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            NotificationTemplateDTO updatedTemplate = systemConfigService.updateTemplate(id, templateDTO);
            return ResponseEntity.ok(ApiResponse.success("Notification template updated", updatedTemplate));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Delete notification template
     */
    @DeleteMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @PathVariable Long id,
            HttpSession session) {
        try {
            Long adminId = (Long) session.getAttribute("userId");
            if (adminId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not logged in"));
            }

            systemConfigService.deleteTemplate(id);
            return ResponseEntity.ok(ApiResponse.success("Notification template deleted", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
