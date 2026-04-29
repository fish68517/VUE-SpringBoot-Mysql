package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 岗位实体类 - 医院发布的实习岗位
 */
@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 所属医院ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Organization hospital;
    
    /**
     * 岗位名称
     */
    @Column(nullable = false, length = 100)
    private String title;
    
    /**
     * 科室
     */
    @Column(nullable = false, length = 100)
    private String department;
    
    /**
     * 岗位描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 岗位名额
     */
    @Column(nullable = false)
    private Integer quota;
    
    /**
     * 实习周期（天数）
     */
    @Column(nullable = false)
    private Integer duration;
    
    /**
     * 面向学校范围 - JSON格式存储学校ID列表
     */
    @Column(columnDefinition = "JSON")
    private String visibleSchools;
    
    /**
     * 岗位状态: DRAFT(草稿), PUBLISHED(已发布), ARCHIVED(已下架)
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
