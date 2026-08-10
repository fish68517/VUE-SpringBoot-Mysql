package com.charging.platform.controller;

import com.charging.platform.common.Result;
import com.charging.platform.service.StatisticsService;
import com.charging.platform.vo.RegionStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/regions")
    public Result<List<RegionStatisticsVO>> regions(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate statDate) {
        return Result.success(statisticsService.getRegionRanking(statDate));
    }

    @GetMapping("/trend")
    public Result<List<RegionStatisticsVO>> trend(
            @RequestParam Long regionId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getTrend(regionId, startDate, endDate));
    }
}
