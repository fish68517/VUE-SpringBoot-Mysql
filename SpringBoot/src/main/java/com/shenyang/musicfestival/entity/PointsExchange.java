package com.shenyang.musicfestival.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "points_exchanges")
@Data
public class PointsExchange extends BaseEntity {
    private Long userId;
    private Long mallItemId;
    private Integer pointsUsed;
    private String status;
    private String shippingAddress;
    private String trackingNumber;
}