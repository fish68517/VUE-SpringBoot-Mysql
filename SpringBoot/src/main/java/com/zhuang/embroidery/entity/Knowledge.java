package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 知识实体类
 */
@Entity
@Table(name = "knowledge", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Knowledge {

    /**
     * 知识ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文章标题
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 文章内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 分类：技法知识/历史文化/政策法规/常见问题
     */
    @Column(length = 50)
    private String category;

    /**
     * 作者
     */
    @Column(length = 100)
    private String author;

    /**
     * 浏览次数
     */
    @Column(nullable = false)
    private Integer viewCount;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 创建前自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (viewCount == null) {
            viewCount = 0;
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
