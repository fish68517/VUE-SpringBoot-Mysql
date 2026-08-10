package com.charging.platform.task;

import com.charging.platform.service.SimulationService;
import com.charging.platform.service.StatisticsGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@ConditionalOnProperty(name = "simulation.enabled", havingValue = "true")
public class ChargingSimulationTask {

    private final SimulationService simulationService;
    private final StatisticsGenerationService statisticsGenerationService;

    public ChargingSimulationTask(SimulationService simulationService,
                                  StatisticsGenerationService statisticsGenerationService) {
        this.simulationService = simulationService;
        this.statisticsGenerationService = statisticsGenerationService;
    }

    @Scheduled(initialDelayString = "${simulation.initial-delay-ms:60000}",
            fixedDelayString = "${simulation.fixed-delay-ms:900000}")
    public void simulateStatus() {
        log.info("充电桩模拟任务完成：{}", simulationService.runOnce());
    }

    @Scheduled(cron = "${simulation.statistics-cron:0 10 0 * * ?}")
    public void generateDailyStatistics() {
        LocalDate date = LocalDate.now().minusDays(1);
        log.info("区域统计生成完成：date={}, rows={}", date, statisticsGenerationService.regenerate(date));
    }
}
