package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_station")
public class ChargingStation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long regionId;
    private String stationCode;
    private String stationName;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer stationType;
    private String openTime;
    private String operatorName;
    private String parkingDesc;
    private String feeDesc;
    private String serviceFacilities;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
