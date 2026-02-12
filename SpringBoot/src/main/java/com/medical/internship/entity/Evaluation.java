package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 评价实体类 - 老师对学生或学生对老师的评价
 */
@Entity
@Table(name = "evaluation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {
    
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
     * 评价人ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private User evaluator;
    
    /**
     * 评价人类型: TEACHER(老师), STUDENT(学生)
     */
    @Column(nullable = false, length = 20)
    private String evaluatorType;
    
    /**
     * 评分
     */
    @Column(nullable = false)
    private Integer score;
    
    /**
     * 评价内容
     */
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
