package com.sharkfitness.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CheckIn entity for daily check-in tracking
 */
@Entity
@Table(name = "check_in",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "check_date"}))
@Data
public class CheckIn {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;
    
    @Column(name = "check_time")
    private LocalDateTime checkTime;
}
