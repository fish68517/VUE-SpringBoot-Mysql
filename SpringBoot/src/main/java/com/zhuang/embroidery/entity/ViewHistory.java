package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 浏览历史实体类
 */
@Entity
@Table(name = "view_history", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_content_type", columnList = "content_type"),
    @Index(name = "idx_viewed_at", columnList = "viewed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewHistory {

    /**
     * 浏览历史ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(nullable = false,name = "user_id")
    private Long userId;

    /**
     * 内容类型：artwork/knowledge
     */
    @Column(nullable = false, length = 50,name = "content_type")
    private String contentType;

    /**
     * 内容ID
     */
    @Column(nullable = false)
    private Long contentId;

    /**
     * 浏览时间
     */
    @Column(nullable = false,name = "viewed_at")
    private LocalDateTime viewedAt;

    /**
     * 创建前自动设置浏览时间
     */
    @PrePersist
    protected void onCreate() {
        if (viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
    }
}
