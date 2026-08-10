package com.charging.platform.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StationDetailVO {
    private Long id;
    private Long regionId;
    private String regionName;
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
    private Long pileCount;
    private Long freeCount;
    private Long usingCount;
    private Long reservedCount;
    private Long offlineCount;
    private Long faultCount;
}
