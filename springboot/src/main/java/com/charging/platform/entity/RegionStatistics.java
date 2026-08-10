package com.charging.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("region_statistics")
public class RegionStatistics {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long regionId;
    private LocalDate statDate;
    private Integer stationCount;
    private Integer pileCount;
    private Integer freeCount;
    private Integer usingCount;
    private Integer faultCount;
    private Integer usageCount;
    private BigDecimal energyKwh;
    private BigDecimal usageRate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
