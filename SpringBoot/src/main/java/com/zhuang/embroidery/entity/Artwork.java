package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 作品实体类
 */
@Entity
@Table(name = "artwork", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artwork {

    /**
     * 作品ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 作品名称
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 作品描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 分类：日常生活类/节日母题类/针法风格类
     */
    @Column(length = 50)
    private String category;

    /**
     * 作品图片URL
     */
    @Column(length = 255)
    private String imageUrl;

    /**
     * 创作者名称
     */
    @Column(length = 100)
    private String creator;

    /**
     * 刺绣技法
     */
    @Column(length = 100)
    private String technique;

    /**
     * 状态：draft/approved/rejected/offline
     */
    @Column(length = 20)
    private String status;

    /**
     * 浏览次数
     */
    @Column(nullable = false)
    private Integer viewCount;

    /**
     * 收藏次数
     */
    @Column(nullable = false)
    private Integer collectCount;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建前自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "draft";
        }
        if (viewCount == null) {
            viewCount = 0;
        }
        if (collectCount == null) {
            collectCount = 0;
        }
    }

    /**
     * 更新前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
