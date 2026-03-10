package com.shenyang.musicfestival.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "points_history")
@Data
public class PointsHistory extends BaseEntity {
    private Long userId;
    private Integer changeAmount;
    private String changeType;
    private Long relatedId;
    private String description;
}