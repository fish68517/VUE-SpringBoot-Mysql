package com.tourism.repository;

import com.tourism.entity.HotelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 酒店房间数据访问接口
 */
@Repository
public interface HotelRoomRepository extends JpaRepository<HotelRoom, Long> {
    
    /**
     * 根据酒店ID查询所有房间
     * @param hotelId 酒店ID
     * @return 房间列表
     */
    List<HotelRoom> findByHotelId(Long hotelId);
    
    /**
     * 根据酒店ID和房间类型查询房间
     * @param hotelId 酒店ID
     * @param roomType 房间类型
     * @return 房间对象
     */
    Optional<HotelRoom> findByHotelIdAndRoomType(Long hotelId, String roomType);
    
    /**
     * 检查酒店是否有指定类型的房间
     * @param hotelId 酒店ID
     * @param roomType 房间类型
     * @return 是否存在
     */
    boolean existsByHotelIdAndRoomType(Long hotelId, String roomType);
    
    /**
     * 删除酒店的所有房间
     * @param hotelId 酒店ID
     */
    void deleteByHotelId(Long hotelId);
}
