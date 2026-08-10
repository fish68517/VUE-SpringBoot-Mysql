package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("charging_pile")
public class ChargingPile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long stationId;
    private String pileCode;
    private String pileName;
    private Integer connectorType;
    private BigDecimal ratedPower;
    private String manufacturer;
    private LocalDate installDate;
    private Integer enableStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
