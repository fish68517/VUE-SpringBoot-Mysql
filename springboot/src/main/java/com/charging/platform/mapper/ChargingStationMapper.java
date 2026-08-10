package com.charging.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.entity.ChargingStation;
import com.charging.platform.vo.StationDetailVO;
import com.charging.platform.vo.StationListVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ChargingStationMapper extends BaseMapper<ChargingStation> {

    String LATEST_STATUS_JOIN = """
            LEFT JOIN (
                SELECT s1.*
                FROM pile_realtime_status s1
                INNER JOIN (
                    SELECT pile_id, MAX(id) AS max_id
                    FROM pile_realtime_status
                    GROUP BY pile_id
                ) s2 ON s1.id = s2.max_id
            ) prs ON prs.pile_id = p.id
            """;

    @Select("""
            <script>
            SELECT cs.id,
                   cs.region_id,
                   r.region_name,
                   cs.station_code,
                   cs.station_name,
                   cs.address,
                   cs.longitude,
                   cs.latitude,
                   cs.station_type,
                   cs.open_time,
                   cs.operator_name,
                   cs.status,
                   COUNT(p.id) AS pile_count,
                   SUM(CASE WHEN prs.work_status = 0 THEN 1 ELSE 0 END) AS free_count,
                   SUM(CASE WHEN prs.work_status = 1 THEN 1 ELSE 0 END) AS using_count,
                   SUM(CASE WHEN prs.work_status = 2 THEN 1 ELSE 0 END) AS reserved_count,
                   SUM(CASE WHEN prs.work_status = 3 THEN 1 ELSE 0 END) AS offline_count,
                   SUM(CASE WHEN prs.work_status = 4 THEN 1 ELSE 0 END) AS fault_count
            FROM charging_station cs
            JOIN region r ON r.id = cs.region_id AND r.deleted = 0
            LEFT JOIN charging_pile p ON p.station_id = cs.id AND p.deleted = 0
            """ + LATEST_STATUS_JOIN + """
            WHERE cs.deleted = 0
            <if test="keyword != null and keyword != ''">
                AND (cs.station_name LIKE CONCAT('%', #{keyword}, '%')
                     OR cs.address LIKE CONCAT('%', #{keyword}, '%')
                     OR cs.station_code LIKE CONCAT('%', #{keyword}, '%'))
            </if>
            <if test="regionId != null">AND cs.region_id = #{regionId}</if>
            <if test="stationType != null">AND cs.station_type = #{stationType}</if>
            <if test="status != null">AND cs.status = #{status}</if>
            <if test="connectorType != null">
                AND EXISTS (
                    SELECT 1 FROM charging_pile cp
                    WHERE cp.station_id = cs.id
                      AND cp.connector_type = #{connectorType}
                      AND cp.deleted = 0
                )
            </if>
            GROUP BY cs.id, cs.region_id, r.region_name, cs.station_code, cs.station_name,
                     cs.address, cs.longitude, cs.latitude, cs.station_type, cs.open_time,
                     cs.operator_name, cs.status
            ORDER BY cs.id
            </script>
            """)
    Page<StationListVO> selectStationPage(Page<StationListVO> page,
                                          @Param("keyword") String keyword,
                                          @Param("regionId") Long regionId,
                                          @Param("stationType") Integer stationType,
                                          @Param("connectorType") Integer connectorType,
                                          @Param("status") Integer status);

    @Select("""
            SELECT cs.id,
                   cs.region_id,
                   r.region_name,
                   cs.station_code,
                   cs.station_name,
                   cs.address,
                   cs.longitude,
                   cs.latitude,
                   cs.station_type,
                   cs.open_time,
                   cs.operator_name,
                   cs.parking_desc,
                   cs.fee_desc,
                   cs.service_facilities,
                   cs.status,
                   COUNT(p.id) AS pile_count,
                   SUM(CASE WHEN prs.work_status = 0 THEN 1 ELSE 0 END) AS free_count,
                   SUM(CASE WHEN prs.work_status = 1 THEN 1 ELSE 0 END) AS using_count,
                   SUM(CASE WHEN prs.work_status = 2 THEN 1 ELSE 0 END) AS reserved_count,
                   SUM(CASE WHEN prs.work_status = 3 THEN 1 ELSE 0 END) AS offline_count,
                   SUM(CASE WHEN prs.work_status = 4 THEN 1 ELSE 0 END) AS fault_count
            FROM charging_station cs
            JOIN region r ON r.id = cs.region_id AND r.deleted = 0
            LEFT JOIN charging_pile p ON p.station_id = cs.id AND p.deleted = 0
            """ + LATEST_STATUS_JOIN + """
            WHERE cs.id = #{id} AND cs.deleted = 0
            GROUP BY cs.id, cs.region_id, r.region_name, cs.station_code, cs.station_name,
                     cs.address, cs.longitude, cs.latitude, cs.station_type, cs.open_time,
                     cs.operator_name, cs.parking_desc, cs.fee_desc, cs.service_facilities, cs.status
            """)
    StationDetailVO selectStationDetail(@Param("id") Long id);

    @Select("""
            SELECT cs.id,
                   cs.region_id,
                   r.region_name,
                   cs.station_code,
                   cs.station_name,
                   cs.address,
                   cs.longitude,
                   cs.latitude,
                   cs.station_type,
                   cs.open_time,
                   cs.operator_name,
                   cs.status,
                   COUNT(p.id) AS pile_count,
                   SUM(CASE WHEN prs.work_status = 0 THEN 1 ELSE 0 END) AS free_count,
                   SUM(CASE WHEN prs.work_status = 1 THEN 1 ELSE 0 END) AS using_count,
                   SUM(CASE WHEN prs.work_status = 2 THEN 1 ELSE 0 END) AS reserved_count,
                   SUM(CASE WHEN prs.work_status = 3 THEN 1 ELSE 0 END) AS offline_count,
                   SUM(CASE WHEN prs.work_status = 4 THEN 1 ELSE 0 END) AS fault_count
            FROM charging_station cs
            JOIN region r ON r.id = cs.region_id AND r.deleted = 0
            LEFT JOIN charging_pile p ON p.station_id = cs.id AND p.deleted = 0
            """ + LATEST_STATUS_JOIN + """
            WHERE cs.deleted = 0 AND cs.status = 1
            GROUP BY cs.id, cs.region_id, r.region_name, cs.station_code, cs.station_name,
                     cs.address, cs.longitude, cs.latitude, cs.station_type, cs.open_time,
                     cs.operator_name, cs.status
            ORDER BY free_count DESC, fault_count ASC, cs.id
            LIMIT #{limit}
            """)
    List<StationListVO> selectRecommendations(@Param("limit") int limit);
}
