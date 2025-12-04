package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * TicketZone entity representing ticket zone information
 */
@Entity
@Table(name = "ticket_zones", indexes = {
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketZone extends BaseEntity {

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @Column(name = "sold_count", nullable = false)
    private Integer soldCount = 0;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

}
