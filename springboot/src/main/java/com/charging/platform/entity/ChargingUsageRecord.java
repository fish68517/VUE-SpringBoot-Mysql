package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_usage_record")
public class ChargingUsageRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long pileId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMin;
    private BigDecimal energyKwh;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private Integer recordStatus;
    private LocalDateTime createTime;
}
