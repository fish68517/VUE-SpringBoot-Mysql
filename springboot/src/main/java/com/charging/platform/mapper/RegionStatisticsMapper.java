package com.charging.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.platform.entity.RegionStatistics;
import com.charging.platform.vo.RegionStatisticsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface RegionStatisticsMapper extends BaseMapper<RegionStatistics> {

    @Select("""
            <script>
            SELECT rs.region_id, r.region_name, rs.stat_date, rs.station_count, rs.pile_count,
                   rs.free_count, rs.using_count, rs.fault_count, rs.usage_count,
                   rs.energy_kwh, rs.usage_rate
            FROM region_statistics rs
            JOIN region r ON r.id = rs.region_id AND r.deleted = 0
            WHERE rs.stat_date = COALESCE(#{statDate}, (SELECT MAX(stat_date) FROM region_statistics))
            ORDER BY rs.usage_rate DESC, rs.pile_count DESC, rs.region_id
            </script>
            """)
    List<RegionStatisticsVO> selectRegionRanking(@Param("statDate") LocalDate statDate);

    @Select("""
            SELECT rs.region_id, r.region_name, rs.stat_date, rs.station_count, rs.pile_count,
                   rs.free_count, rs.using_count, rs.fault_count, rs.usage_count,
                   rs.energy_kwh, rs.usage_rate
            FROM region_statistics rs
            JOIN region r ON r.id = rs.region_id AND r.deleted = 0
            WHERE rs.region_id = #{regionId}
              AND rs.stat_date BETWEEN #{startDate} AND #{endDate}
            ORDER BY rs.stat_date
            """)
    List<RegionStatisticsVO> selectTrend(@Param("regionId") Long regionId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
