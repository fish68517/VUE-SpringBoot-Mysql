package com.charging.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.common.PageResult;
import com.charging.platform.dto.StationQuery;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.ChargingStationMapper;
import com.charging.platform.vo.StationDetailVO;
import com.charging.platform.vo.StationListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final ChargingStationMapper stationMapper;

    public PageResult<StationListVO> getStationPage(StationQuery query) {
        Page<StationListVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        String keyword = StringUtils.hasText(query.getKeyword()) ? query.getKeyword().trim() : null;
        Page<StationListVO> result = stationMapper.selectStationPage(
                page,
                keyword,
                query.getRegionId(),
                query.getStationType(),
                query.getConnectorType(),
                query.getStatus());
        return PageResult.from(result);
    }

    public StationDetailVO getStationDetail(Long id) {
        StationDetailVO detail = stationMapper.selectStationDetail(id);
        if (detail == null) {
            throw new BusinessException(404, "充电站不存在");
        }
        return detail;
    }

    public List<StationListVO> getRecommendations(int limit) {
        return stationMapper.selectRecommendations(limit);
    }
}
