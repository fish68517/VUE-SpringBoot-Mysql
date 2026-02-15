package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "user", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_role", columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名（唯一）
     */
    @Column(nullable = false, unique = true, length = 50,name = "username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 头像URL
     */
    @Column(length = 255)
    private String avatar;

    /**
     * 个人简介
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * 角色：user/admin
     */
    @Column(length = 20)
    private String role;

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
        if (role == null) {
            role = "user";
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
