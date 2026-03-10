package com.shenyang.musicfestival.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "points_mall")
@Data
public class PointsMallItem extends BaseEntity {
    private String name;
    private String description;
    private String images; // JSON 格式存储图片组
    private Integer pointsRequired;
    private String type;
    private Integer stock;
    private LocalDateTime countdownEndTime;
    private Boolean isActive;
}