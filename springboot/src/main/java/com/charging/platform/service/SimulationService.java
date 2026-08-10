package com.charging.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.charging.platform.entity.ChargingPile;
import com.charging.platform.entity.ChargingUsageRecord;
import com.charging.platform.entity.PileRealtimeStatus;
import com.charging.platform.mapper.ChargingPileMapper;
import com.charging.platform.mapper.ChargingUsageRecordMapper;
import com.charging.platform.mapper.PileRealtimeStatusMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SimulationService {

    private final ChargingPileMapper pileMapper;
    private final PileRealtimeStatusMapper statusMapper;
    private final ChargingUsageRecordMapper usageRecordMapper;

    public SimulationService(ChargingPileMapper pileMapper,
                             PileRealtimeStatusMapper statusMapper,
                             ChargingUsageRecordMapper usageRecordMapper) {
        this.pileMapper = pileMapper;
        this.statusMapper = statusMapper;
        this.usageRecordMapper = usageRecordMapper;
    }

    @Transactional
    public Map<String, Object> runOnce() {
        List<ChargingPile> piles = pileMapper.selectList(
                new QueryWrapper<ChargingPile>().eq("enable_status", 1).orderByAsc("id"));
        LocalDateTime now = LocalDateTime.now();
        int usageRecords = 0;
        int faultCount = 0;
        for (ChargingPile pile : piles) {
            int workStatus = randomStatus(now.getHour());
            if (workStatus == 4) faultCount++;

            PileRealtimeStatus status = new PileRealtimeStatus();
            status.setPileId(pile.getId());
            status.setWorkStatus(workStatus);
            status.setCurrentPower(workStatus == 1
                    ? pile.getRatedPower().multiply(BigDecimal.valueOf(random(45, 91) / 100.0))
                        .setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO);
            status.setAlarmCode(workStatus == 4 ? "SIM-E" + random(100, 999) : null);
            status.setStatusTime(now);
            status.setSourceType(0);
            statusMapper.insert(status);

            if (workStatus == 1 && random(0, 100) < 25) {
                createUsageRecord(pile, now);
                usageRecords++;
            }
        }
        return Map.of(
                "updatedPileCount", piles.size(),
                "generatedUsageCount", usageRecords,
                "faultCount", faultCount,
                "runTime", now
        );
    }

    private int randomStatus(int hour) {
        int value = random(0, 1000);
        if (value < 30) return 4;
        if (value < 50) return 3;
        if (value < 120) return 2;
        int usingThreshold = (hour >= 8 && hour <= 10) || (hour >= 17 && hour <= 21) ? 520 : 340;
        if (value < usingThreshold) return 1;
        return 0;
    }

    private void createUsageRecord(ChargingPile pile, LocalDateTime now) {
        int duration = random(20, 91);
        BigDecimal energy = pile.getRatedPower()
                .multiply(BigDecimal.valueOf(duration / 60.0))
                .multiply(BigDecimal.valueOf(random(45, 86) / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal serviceFee = energy.multiply(BigDecimal.valueOf(0.2)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal amount = energy.multiply(BigDecimal.valueOf(0.85)).add(serviceFee)
                .setScale(2, RoundingMode.HALF_UP);
        ChargingUsageRecord record = new ChargingUsageRecord();
        record.setPileId(pile.getId());
        record.setUserId(random(0, 2) == 0 ? 2L : 3L);
        record.setStartTime(now.minusMinutes(duration));
        record.setEndTime(now);
        record.setDurationMin(duration);
        record.setEnergyKwh(energy);
        record.setServiceFee(serviceFee);
        record.setTotalAmount(amount);
        record.setRecordStatus(1);
        usageRecordMapper.insert(record);
    }

    private int random(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}
