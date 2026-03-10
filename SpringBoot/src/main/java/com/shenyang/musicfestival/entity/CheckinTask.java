package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkin_tasks")
@Data
public class CheckinTask extends BaseEntity {
    private String name;
    private String description;
    private String coverImage;
    private Integer points;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer radius;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}