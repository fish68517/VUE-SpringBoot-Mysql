package com.charging.platform.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegionStatisticsVO {
    private Long regionId;
    private String regionName;
    private LocalDate statDate;
    private Integer stationCount;
    private Integer pileCount;
    private Integer freeCount;
    private Integer usingCount;
    private Integer faultCount;
    private Integer usageCount;
    private BigDecimal energyKwh;
    private BigDecimal usageRate;
}
