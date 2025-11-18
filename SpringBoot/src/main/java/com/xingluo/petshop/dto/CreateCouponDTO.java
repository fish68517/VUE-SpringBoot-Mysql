package com.xingluo.petshop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建优惠券 DTO
 */
@Data
public class CreateCouponDTO {
    
    private Long shopId;
    
    private String name;
    
    private BigDecimal discountAmount;
    
    private BigDecimal minAmount;
    
    private Integer totalCount;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
}
