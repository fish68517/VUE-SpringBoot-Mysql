package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 系统设置实体类
 */
@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettings {

    /**
     * 系统设置ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 网站名称
     */
    @Column(nullable = false, length = 255)
    private String siteName;

    /**
     * 网站描述
     */
    @Column(columnDefinition = "TEXT")
    private String siteDescription;

    /**
     * logo URL
     */
    @Column(length = 255)
    private String logo;

    /**
     * 联系邮箱
     */
    @Column(length = 100)
    private String contactEmail;

    /**
     * 联系电话
     */
    @Column(length = 20)
    private String contactPhone;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建前自动设置更新时间
     */
    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 更新前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
