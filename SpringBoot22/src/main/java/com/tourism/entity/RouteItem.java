package com.tourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 旅游路线项目实体类（路线中的景点/酒店）
 */
@Entity
@Table(name = "route_items", indexes = {
    @Index(name = "idx_route_id", columnList = "route_id"),
    @Index(name = "idx_route_day", columnList = "route_id, day_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long routeId;
    
    @Column(nullable = false)
    private Integer dayNumber;
    
    @Column(nullable = false, length = 50)
    private String itemType;
    
    @Column(nullable = false)
    private Long itemId;
    
    @Column(nullable = false)
    private Integer sequence;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
