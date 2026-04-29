package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    /**
     * 密码
     */
    @Column(nullable = false, length = 255)
    private String password;
    
    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;
    
    /**
     * 用户角色: ADMIN(系统管理员), SCHOOL_ADMIN(学校管理员), HOSPITAL_ADMIN(医院管理员), STUDENT(学生), TEACHER(带教老师)
     */
    @Column(nullable = false, length = 20)
    private String role;
    
    /**
     * 所属组织ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    
    /**
     * 用户状态: PENDING(待审核), APPROVED(已批准), REJECTED(已驳回)
     */
    @Column(nullable = false, length = 20)
    private String status;
    
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
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
