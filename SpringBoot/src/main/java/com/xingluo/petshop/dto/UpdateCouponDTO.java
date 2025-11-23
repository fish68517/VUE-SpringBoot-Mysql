package com.xingluo.petshop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateCouponDTO {
    private String name;
    private BigDecimal discountAmount;
    private BigDecimal minAmount;
    private Integer totalCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
}