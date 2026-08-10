package com.charging.platform.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PileStatusVO {
    private Long id;
    private Long stationId;
    private String pileCode;
    private String pileName;
    private Integer connectorType;
    private BigDecimal ratedPower;
    private String manufacturer;
    private LocalDate installDate;
    private Integer enableStatus;
    private Integer workStatus;
    private BigDecimal currentPower;
    private String alarmCode;
    private LocalDateTime statusTime;
}
