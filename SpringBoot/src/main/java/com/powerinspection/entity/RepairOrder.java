package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "repair_order")
public class RepairOrder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "defect_id", nullable = false, unique = true)
    private Defect defect;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maintainer_id", nullable = false)
    private User maintainer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(length = 500)
    private String measures;

    @Column(length = 500)
    private String result;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(length = 255)
    private String remark;
}
