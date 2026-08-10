package com.charging.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.platform.entity.ChargingPile;
import com.charging.platform.vo.PileStatusVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChargingPileMapper extends BaseMapper<ChargingPile> {

    @Select("""
            SELECT p.id, p.station_id, p.pile_code, p.pile_name, p.connector_type,
                   p.rated_power, p.manufacturer, p.install_date, p.enable_status,
                   prs.work_status, prs.current_power, prs.alarm_code, prs.status_time
            FROM charging_pile p
            LEFT JOIN pile_realtime_status prs ON prs.id = (
                SELECT s.id
                FROM pile_realtime_status s
                WHERE s.pile_id = p.id
                ORDER BY s.status_time DESC, s.id DESC
                LIMIT 1
            )
            WHERE p.station_id = #{stationId} AND p.deleted = 0
            ORDER BY p.id
            """)
    List<PileStatusVO> selectPilesWithLatestStatus(@Param("stationId") Long stationId);

    @Select("""
            SELECT p.id, p.station_id, p.pile_code, p.pile_name, p.connector_type,
                   p.rated_power, p.manufacturer, p.install_date, p.enable_status,
                   prs.work_status, prs.current_power, prs.alarm_code, prs.status_time
            FROM charging_pile p
            LEFT JOIN pile_realtime_status prs ON prs.id = (
                SELECT s.id
                FROM pile_realtime_status s
                WHERE s.pile_id = p.id
                ORDER BY s.status_time DESC, s.id DESC
                LIMIT 1
            )
            WHERE p.id = #{id} AND p.deleted = 0
            """)
    PileStatusVO selectPileWithLatestStatus(@Param("id") Long id);
}
