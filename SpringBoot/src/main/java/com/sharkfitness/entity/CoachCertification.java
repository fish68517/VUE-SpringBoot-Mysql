package com.sharkfitness.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coach_certification")
@Data
public class CoachCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long coachId;
    private String name;
    private String issuingAuthority;
    private String imageUrl;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private Integer status; // 0-Pending, 1-Approved, 2-Rejected
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}