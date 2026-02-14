package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.SystemSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统设置响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsResponse {

    /**
     * 系统设置ID
     */
    private Long id;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站描述
     */
    private String siteDescription;

    /**
     * logo URL
     */
    private String logo;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 SystemSettings 实体转换为 SystemSettingsResponse
     */
    public static SystemSettingsResponse fromSystemSettings(SystemSettings settings) {
        return SystemSettingsResponse.builder()
                .id(settings.getId())
                .siteName(settings.getSiteName())
                .siteDescription(settings.getSiteDescription())
                .logo(settings.getLogo())
                .contactEmail(settings.getContactEmail())
                .contactPhone(settings.getContactPhone())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }

}
