package com.tourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Entity
@Table(name = "favorites", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_target_type_id", columnList = "target_type, target_id"),
    @Index(name = "idx_unique_favorite", columnList = "user_id, target_type, target_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 20)
    private String targetType;
    
    @Column(nullable = false)
    private Long targetId;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
