package com.tourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 留言实体类
 */
@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_target_type_id", columnList = "target_type, target_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_is_pinned", columnList = "is_pinned")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 20)
    private String targetType;
    
    @Column(nullable = false)
    private Long targetId;
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    @Column
    private Integer rating;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    @Column(nullable = false)
    private Boolean isPinned;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isPinned == null) {
            isPinned = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
