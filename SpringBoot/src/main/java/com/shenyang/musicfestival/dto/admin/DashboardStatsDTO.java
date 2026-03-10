package com.shenyang.musicfestival.dto.admin;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsDTO {
    private Long todayOrderCount;
    private Long weekOrderCount;
    private Long monthOrderCount;
    private List<Map<String, Object>> userGrowth;
    private List<Map<String, Object>> ticketHeatmap;
    private List<Map<String, Object>> topProducts;
    private Map<String, Object> taskStats;
    private Map<String, String> weather;
    private Integer festivalCountdown;
}