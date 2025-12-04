package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TicketSession entity representing ticket session information
 */
@Entity
@Table(name = "ticket_sessions", indexes = {
    @Index(name = "idx_festival_id", columnList = "festival_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_start_time", columnList = "start_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSession extends BaseEntity {

    @Column(name = "festival_id", nullable = false)
    private Long festivalId;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "status", length = 50)
    private String status = "available";  // available, sold_out, ended

}
