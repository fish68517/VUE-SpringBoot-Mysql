package com.charging.platform.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class StatisticsGenerationService {

    private final JdbcTemplate jdbcTemplate;

    public StatisticsGenerationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public int regenerate(LocalDate statDate) {
        String sql = """
                INSERT INTO region_statistics
                    (region_id, stat_date, station_count, pile_count, free_count, using_count,
                     fault_count, usage_count, energy_kwh, usage_rate)
                SELECT cs.region_id,
                       ?,
                       COUNT(DISTINCT cs.id),
                       COUNT(DISTINCT p.id),
                       SUM(CASE WHEN latest.work_status = 0 THEN 1 ELSE 0 END),
                       SUM(CASE WHEN latest.work_status = 1 THEN 1 ELSE 0 END),
                       SUM(CASE WHEN latest.work_status = 4 THEN 1 ELSE 0 END),
                       COALESCE(SUM(usage_data.usage_count), 0),
                       COALESCE(SUM(usage_data.energy_kwh), 0),
                       CASE WHEN COUNT(DISTINCT p.id) = 0 THEN 0
                            ELSE ROUND(SUM(CASE WHEN latest.work_status = 1 THEN 1 ELSE 0 END)
                                 / COUNT(DISTINCT p.id) * 100, 2) END
                FROM charging_station cs
                LEFT JOIN charging_pile p ON p.station_id = cs.id AND p.deleted = 0
                LEFT JOIN (
                    SELECT s1.pile_id, s1.work_status
                    FROM pile_realtime_status s1
                    INNER JOIN (
                        SELECT pile_id, MAX(id) max_id
                        FROM pile_realtime_status
                        GROUP BY pile_id
                    ) s2 ON s1.id = s2.max_id
                ) latest ON latest.pile_id = p.id
                LEFT JOIN (
                    SELECT pile_id, COUNT(*) usage_count, COALESCE(SUM(energy_kwh), 0) energy_kwh
                    FROM charging_usage_record
                    WHERE DATE(start_time) = ?
                    GROUP BY pile_id
                ) usage_data ON usage_data.pile_id = p.id
                WHERE cs.deleted = 0
                GROUP BY cs.region_id
                ON DUPLICATE KEY UPDATE
                    station_count = VALUES(station_count),
                    pile_count = VALUES(pile_count),
                    free_count = VALUES(free_count),
                    using_count = VALUES(using_count),
                    fault_count = VALUES(fault_count),
                    usage_count = VALUES(usage_count),
                    energy_kwh = VALUES(energy_kwh),
                    usage_rate = VALUES(usage_rate),
                    update_time = CURRENT_TIMESTAMP
                """;
        return jdbcTemplate.update(sql, Date.valueOf(statDate), Date.valueOf(statDate));
    }
}
