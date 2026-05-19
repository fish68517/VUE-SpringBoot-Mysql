package com.shenyang.musicfestival.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "checkin_records")
@Data
public class CheckinRecord extends BaseEntity {
    private Long userId;
    private Long taskId;
    private String photo;
    @Column(length = 1000)
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String status;
    private String rejectReason;
}
