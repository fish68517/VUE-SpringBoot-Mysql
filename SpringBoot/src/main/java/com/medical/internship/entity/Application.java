package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 申请实体类 - 学生对岗位的申请记录
 */
@Entity
@Table(name = "application")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 申请学生ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    /**
     * 申请岗位ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    /**
     * 申请理由
     */
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    /**
     * 学校审批状态: PENDING(待审核), APPROVED(已批准), REJECTED(已驳回)
     */
    @Column(nullable = false, length = 20)
    private String schoolStatus;
    
    /**
     * 医院审批状态: PENDING(待审核), APPROVED(已批准), REJECTED(已驳回)
     */
    @Column(nullable = false, length = 20)
    private String hospitalStatus;
    
    /**
     * 学校审批意见
     */
    @Column(columnDefinition = "TEXT")
    private String schoolOpinion;
    
    /**
     * 医院审批意见
     */
    @Column(columnDefinition = "TEXT")
    private String hospitalOpinion;
    
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
