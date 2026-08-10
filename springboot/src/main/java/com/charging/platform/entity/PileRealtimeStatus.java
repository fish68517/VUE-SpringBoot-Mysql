package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pile_realtime_status")
public class PileRealtimeStatus {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long pileId;
    private Integer workStatus;
    private BigDecimal currentPower;
    private String alarmCode;
    private LocalDateTime statusTime;
    private Integer sourceType;
    private LocalDateTime createTime;
}
