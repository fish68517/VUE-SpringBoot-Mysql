package com.charging.platform.service;

import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.RegionMapper;
import com.charging.platform.mapper.RegionStatisticsMapper;
import com.charging.platform.vo.RegionStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final RegionStatisticsMapper statisticsMapper;
    private final RegionMapper regionMapper;

    public List<RegionStatisticsVO> getRegionRanking(LocalDate statDate) {
        return statisticsMapper.selectRegionRanking(statDate);
    }

    public List<RegionStatisticsVO> getTrend(Long regionId, LocalDate startDate, LocalDate endDate) {
        if (regionMapper.selectById(regionId) == null) {
            throw new BusinessException(404, "区域不存在");
        }
        LocalDate actualEndDate = endDate == null ? LocalDate.now() : endDate;
        LocalDate actualStartDate = startDate == null ? actualEndDate.minusDays(6) : startDate;
        if (actualStartDate.isAfter(actualEndDate)) {
            throw new BusinessException("开始日期不能晚于结束日期");
        }
        if (actualStartDate.plusYears(1).isBefore(actualEndDate)) {
            throw new BusinessException("单次查询日期范围不能超过一年");
        }
        return statisticsMapper.selectTrend(regionId, actualStartDate, actualEndDate);
    }
}
