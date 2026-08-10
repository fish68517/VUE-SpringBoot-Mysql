package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.platform.entity.PileRealtimeStatus;
import com.charging.platform.exception.BusinessException;
import com.charging.platform.mapper.ChargingPileMapper;
import com.charging.platform.mapper.PileRealtimeStatusMapper;
import com.charging.platform.vo.PileStatusVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PileService {

    private final ChargingPileMapper pileMapper;
    private final PileRealtimeStatusMapper statusMapper;

    public List<PileStatusVO> getStationPiles(Long stationId) {
        return pileMapper.selectPilesWithLatestStatus(stationId);
    }

    public PileStatusVO getPile(Long id) {
        PileStatusVO pile = pileMapper.selectPileWithLatestStatus(id);
        if (pile == null) {
            throw new BusinessException(404, "充电桩不存在");
        }
        return pile;
    }

    public List<PileRealtimeStatus> getStatusHistory(Long pileId, int limit) {
        getPile(pileId);
        return statusMapper.selectList(new LambdaQueryWrapper<PileRealtimeStatus>()
                .eq(PileRealtimeStatus::getPileId, pileId)
                .orderByDesc(PileRealtimeStatus::getStatusTime, PileRealtimeStatus::getId)
                .last("LIMIT " + limit));
    }
}
