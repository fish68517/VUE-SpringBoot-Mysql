package com.charging.platform.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteVO {
    private Long favoriteId;
    private Long stationId;
    private String stationName;
    private String regionName;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer status;
    private Long pileCount;
    private Long freeCount;
    private LocalDateTime createTime;
}
