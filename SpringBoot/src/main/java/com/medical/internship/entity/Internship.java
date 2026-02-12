package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实习记录实体类 - 申请通过后生成的实习过程记录
 */
@Entity
@Table(name = "internship")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Internship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的申请ID
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
    
    /**
     * 学生ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    /**
     * 带教老师ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;
    
    /**
     * 岗位ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    /**
     * 实习开始日期
     */
    @Column(nullable = false)
    private LocalDate startDate;
    
    /**
     * 实习结束日期
     */
    @Column(nullable = false)
    private LocalDate endDate;
    
    /**
     * 实习状态: ONGOING(进行中), COMPLETED(已完成), TERMINATED(已终止)
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
