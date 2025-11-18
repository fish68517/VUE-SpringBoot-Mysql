package com.xingluo.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Data
@Entity
@Table(name = "user_coupon")
public class UserCoupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", insertable = false, updatable = false)
    private Coupon coupon;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private Order order;
    
    @Column(nullable = false)
    private Integer status = 0;
    
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;
    
    @Column(name = "use_time")
    private LocalDateTime useTime;
    
    @PrePersist
    protected void onCreate() {
        if (receiveTime == null) {
            receiveTime = LocalDateTime.now();
        }
    }
}
