package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.platform.entity.GuidePoint;
import com.charging.platform.mapper.GuidePointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GuidePointMapper guidePointMapper;
    private final StationService stationService;

    public List<GuidePoint> getGuidePoints(Long stationId) {
        stationService.getStationDetail(stationId);
        return guidePointMapper.selectList(new LambdaQueryWrapper<GuidePoint>()
                .eq(GuidePoint::getStationId, stationId)
                .eq(GuidePoint::getStatus, 1)
                .orderByAsc(GuidePoint::getSortNo, GuidePoint::getId));
    }
}
