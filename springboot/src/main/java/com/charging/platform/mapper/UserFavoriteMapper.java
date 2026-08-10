package com.charging.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.platform.entity.UserFavorite;
import com.charging.platform.vo.FavoriteVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    @Select("""
            SELECT uf.id AS favorite_id, cs.id AS station_id, cs.station_name, r.region_name,
                   cs.address, cs.longitude, cs.latitude, cs.status,
                   COUNT(p.id) AS pile_count,
                   SUM(CASE WHEN prs.work_status = 0 THEN 1 ELSE 0 END) AS free_count,
                   uf.create_time
            FROM user_favorite uf
            JOIN charging_station cs ON cs.id = uf.station_id AND cs.deleted = 0
            JOIN region r ON r.id = cs.region_id AND r.deleted = 0
            LEFT JOIN charging_pile p ON p.station_id = cs.id AND p.deleted = 0
            LEFT JOIN pile_realtime_status prs ON prs.id = (
                SELECT s.id FROM pile_realtime_status s
                WHERE s.pile_id = p.id
                ORDER BY s.status_time DESC, s.id DESC
                LIMIT 1
            )
            WHERE uf.user_id = #{userId}
            GROUP BY uf.id, cs.id, cs.station_name, r.region_name, cs.address,
                     cs.longitude, cs.latitude, cs.status, uf.create_time
            ORDER BY uf.create_time DESC, uf.id DESC
            """)
    Page<FavoriteVO> selectFavoritePage(Page<FavoriteVO> page, @Param("userId") Long userId);
}
