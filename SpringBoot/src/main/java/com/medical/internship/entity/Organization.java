package com.medical.internship.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 组织实体类 - 代表学校或医院
 */
@Entity
@Table(name = "organization")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 组织名称
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * 组织类型: SCHOOL(学校) 或 HOSPITAL(医院)
     */
    @Column(nullable = false, length = 20)
    private String type;
    
    /**
     * 组织代码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
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
