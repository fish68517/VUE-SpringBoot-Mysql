package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统设置更新请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsUpdateRequest {

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

}
