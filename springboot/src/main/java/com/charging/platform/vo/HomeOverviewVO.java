package com.charging.platform.vo;

import lombok.Data;

@Data
public class HomeOverviewVO {
    private Long regionCount;
    private Long stationCount;
    private Long pileCount;
    private Long freeCount;
    private Long usingCount;
    private Long reservedCount;
    private Long offlineCount;
    private Long faultCount;
    private Long publishedNoticeCount;
}
