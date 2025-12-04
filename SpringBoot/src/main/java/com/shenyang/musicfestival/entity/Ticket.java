package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ticket entity representing electronic tickets
 */
@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_zone_id", columnList = "zone_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "created_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_session_id_number", columnNames = {"session_id", "buyer_id_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "zone_id", nullable = false)
    private Long zoneId;

    @Column(name = "buyer_id_number", nullable = false, length = 18)
    private String buyerIdNumber;

    @Column(name = "buyer_name", nullable = false, length = 100)
    private String buyerName;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "status", length = 50)
    private String status = "valid";  // valid, used, expired

}
