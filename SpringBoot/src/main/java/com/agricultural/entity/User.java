package com.agricultural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_user_type", columnList = "user_type"),
    @Index(name = "idx_region", columnList = "region")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    @Column(length = 100)
    private String region;
    
    @Column
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 用户类型枚举
     */
    public enum UserType {
        FARMER,      // 农户
        MERCHANT,    // 农资商家
        ADMIN        // 管理员
    }
    
    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE,      // 活跃
        INACTIVE,    // 非活跃
        LOCKED       // 锁定
    }
}
