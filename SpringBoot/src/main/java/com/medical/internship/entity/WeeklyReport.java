package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 周记实体类 - 学生每周提交的实习总结
 */
@Entity
@Table(name = "weekly_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的实习记录ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;
    
    /**
     * 周次
     */
    @Column(nullable = false)
    private Integer weekNumber;
    
    /**
     * 周记内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    /**
     * 老师评语
     */
    @Column(columnDefinition = "TEXT")
    private String teacherComment;
    
    /**
     * 老师评分
     */
    @Column
    private Integer teacherScore;
    
    /**
     * 周记状态: SUBMITTED(已提交), REVIEWED(已批阅), REJECTED(已打回)
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
