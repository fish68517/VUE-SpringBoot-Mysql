package com.charging.platform.mapper;

import com.charging.platform.vo.HomeOverviewVO;
import org.apache.ibatis.annotations.Select;

public interface HomeMapper {

    @Select("""
            SELECT
                (SELECT COUNT(*) FROM region WHERE status = 1 AND deleted = 0) AS region_count,
                (SELECT COUNT(*) FROM charging_station WHERE deleted = 0) AS station_count,
                (SELECT COUNT(*) FROM charging_pile WHERE deleted = 0) AS pile_count,
                SUM(CASE WHEN latest.work_status = 0 THEN 1 ELSE 0 END) AS free_count,
                SUM(CASE WHEN latest.work_status = 1 THEN 1 ELSE 0 END) AS using_count,
                SUM(CASE WHEN latest.work_status = 2 THEN 1 ELSE 0 END) AS reserved_count,
                SUM(CASE WHEN latest.work_status = 3 THEN 1 ELSE 0 END) AS offline_count,
                SUM(CASE WHEN latest.work_status = 4 THEN 1 ELSE 0 END) AS fault_count,
                (SELECT COUNT(*) FROM notice WHERE publish_status = 1 AND deleted = 0) AS published_notice_count
            FROM (
                SELECT s1.*
                FROM pile_realtime_status s1
                INNER JOIN (
                    SELECT pile_id, MAX(id) AS max_id
                    FROM pile_realtime_status
                    GROUP BY pile_id
                ) s2 ON s1.id = s2.max_id
            ) latest
            """)
    HomeOverviewVO selectOverview();
}
