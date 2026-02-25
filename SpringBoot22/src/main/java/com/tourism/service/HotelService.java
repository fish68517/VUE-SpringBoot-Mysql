package com.tourism.service;

import com.tourism.entity.Hotel;
import com.tourism.entity.HotelRoom;
import com.tourism.exception.BusinessException;
import com.tourism.repository.HotelRepository;
import com.tourism.repository.HotelRoomRepository;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 酒店业务逻辑服务
 */
@Service
public class HotelService {
    
    private static final Logger logger = LoggerUtil.getLogger(HotelService.class);
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private HotelRoomRepository hotelRoomRepository;
    
    /**
     * 获取酒店列表（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 酒店分页数据
     */
    public Page<Hotel> getHotelList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findAll(pageable);
        LoggerUtil.info(logger, "获取酒店列表成功 - 页码: " + page + ", 每页数量: " + size);
        return hotels;
    }
    
    /**
     * 根据关键词搜索酒店（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 酒店分页数据
     */
    public Page<Hotel> searchHotels(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.searchByKeyword(keyword, pageable);
        
        LoggerUtil.info(logger, "搜索酒店成功 - 关键词: " + keyword + ", 页码: " + page);
        return hotels;
    }
    
    /**
     * 根据位置查询酒店（分页）
     * @param location 位置
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return 酒店分页数据
     */
    public Page<Hotel> getHotelsByLocation(String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Hotel> hotels = hotelRepository.findByLocation(location, pageable);
        
        LoggerUtil.info(logger, "按位置查询酒店成功 - 位置: " + location + ", 页码: " + page);
        return hotels;
    }
    
    /**
     * 根据ID查询酒店详情
     * @param id 酒店ID
     * @return 酒店对象
     */
    public Hotel getHotelDetail(Long id) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);
        if (!hotelOptional.isPresent()) {
            LoggerUtil.warn(logger, "获取酒店详情失败：酒店不存在 - ID: " + id);
            throw new BusinessException("酒店不存在");
        }
        
        LoggerUtil.info(logger, "获取酒店详情成功 - ID: " + id);
        return hotelOptional.get();
    }
    
    /**
     * 获取酒店的所有房间类型
     * @param hotelId 酒店ID
     * @return 房间列表
     */
    public List<HotelRoom> getHotelRooms(Long hotelId) {
        List<HotelRoom> rooms = hotelRoomRepository.findByHotelId(hotelId);
        LoggerUtil.info(logger, "获取酒店房间成功 - 酒店ID: " + hotelId + ", 房间数: " + rooms.size());
        return rooms;
    }
    
    /**
     * 创建酒店（管理员功能）
     * @param name 酒店名称
     * @param location 位置
     * @param description 酒店描述
     * @param imageUrl 酒店图片
     * @param rating 评分
     * @return 创建的酒店对象
     */
    public Hotel createHotel(String name, String location, String description,
                            String imageUrl, BigDecimal rating) {
        // 检查酒店名称是否已存在
        if (hotelRepository.existsByName(name)) {
            LoggerUtil.warn(logger, "创建酒店失败：酒店名称已存在 - " + name);
            throw new BusinessException("酒店名称已存在");
        }
        
        // 创建新酒店
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setDescription(description);
        hotel.setImageUrl(imageUrl);
        hotel.setRating(rating);
        
        // 保存酒店
        Hotel savedHotel = hotelRepository.save(hotel);
        LoggerUtil.info(logger, "创建酒店成功 - 酒店名称: " + name);
        
        return savedHotel;
    }
    
    /**
     * 更新酒店信息（管理员功能）
     * @param id 酒店ID
     * @param name 酒店名称
     * @param location 位置
     * @param description 酒店描述
     * @param imageUrl 酒店图片
     * @param rating 评分
     * @return 更新后的酒店对象
     */
    public Hotel updateHotel(Long id, String name, String location, String description,
                            String imageUrl, BigDecimal rating) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);
        if (!hotelOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新酒店失败：酒店不存在 - ID: " + id);
            throw new BusinessException("酒店不存在");
        }
        
        Hotel hotel = hotelOptional.get();
        
        // 检查新名称是否已被其他酒店使用
        if (name != null && !name.isEmpty() && !name.equals(hotel.getName())) {
            if (hotelRepository.existsByName(name)) {
                LoggerUtil.warn(logger, "更新酒店失败：酒店名称已存在 - " + name);
                throw new BusinessException("酒店名称已存在");
            }
        }
        
        // 更新酒店信息
        if (name != null && !name.isEmpty()) {
            hotel.setName(name);
        }
        if (location != null && !location.isEmpty()) {
            hotel.setLocation(location);
        }
        if (description != null) {
            hotel.setDescription(description);
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            hotel.setImageUrl(imageUrl);
        }
        if (rating != null) {
            hotel.setRating(rating);
        }
        
        Hotel updatedHotel = hotelRepository.save(hotel);
        LoggerUtil.info(logger, "更新酒店成功 - ID: " + id);
        
        return updatedHotel;
    }
    
    /**
     * 删除酒店（管理员功能）
     * @param id 酒店ID
     */
    public void deleteHotel(Long id) {
        Optional<Hotel> hotelOptional = hotelRepository.findById(id);
        if (!hotelOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除酒店失败：酒店不存在 - ID: " + id);
            throw new BusinessException("酒店不存在");
        }
        
        Hotel hotel = hotelOptional.get();
        
        // 删除酒店的所有房间
        hotelRoomRepository.deleteByHotelId(id);
        
        // 删除酒店
        hotelRepository.deleteById(id);
        LoggerUtil.info(logger, "删除酒店成功 - ID: " + id + ", 酒店名称: " + hotel.getName());
    }
    
    /**
     * 添加酒店房间类型（管理员功能）
     * @param hotelId 酒店ID
     * @param roomType 房间类型
     * @param pricePerNight 每晚价格
     * @param quantity 房间数量
     * @return 创建的房间对象
     */
    public HotelRoom addRoom(Long hotelId, String roomType, BigDecimal pricePerNight, Integer quantity) {
        // 检查酒店是否存在
        if (!hotelRepository.existsById(hotelId)) {
            LoggerUtil.warn(logger, "添加房间失败：酒店不存在 - ID: " + hotelId);
            throw new BusinessException("酒店不存在");
        }
        
        // 检查房间类型是否已存在
        if (hotelRoomRepository.existsByHotelIdAndRoomType(hotelId, roomType)) {
            LoggerUtil.warn(logger, "添加房间失败：房间类型已存在 - 酒店ID: " + hotelId + ", 房间类型: " + roomType);
            throw new BusinessException("房间类型已存在");
        }
        
        // 创建新房间
        HotelRoom room = new HotelRoom();
        room.setHotelId(hotelId);
        room.setRoomType(roomType);
        room.setPricePerNight(pricePerNight);
        room.setQuantity(quantity);
        
        HotelRoom savedRoom = hotelRoomRepository.save(room);
        LoggerUtil.info(logger, "添加房间成功 - 酒店ID: " + hotelId + ", 房间类型: " + roomType);
        
        return savedRoom;
    }
    
    /**
     * 更新酒店房间信息（管理员功能）
     * @param roomId 房间ID
     * @param pricePerNight 每晚价格
     * @param quantity 房间数量
     * @return 更新后的房间对象
     */
    public HotelRoom updateRoom(Long roomId, BigDecimal pricePerNight, Integer quantity) {
        Optional<HotelRoom> roomOptional = hotelRoomRepository.findById(roomId);
        if (!roomOptional.isPresent()) {
            LoggerUtil.warn(logger, "更新房间失败：房间不存在 - ID: " + roomId);
            throw new BusinessException("房间不存在");
        }
        
        HotelRoom room = roomOptional.get();
        
        if (pricePerNight != null) {
            room.setPricePerNight(pricePerNight);
        }
        if (quantity != null) {
            room.setQuantity(quantity);
        }
        
        HotelRoom updatedRoom = hotelRoomRepository.save(room);
        LoggerUtil.info(logger, "更新房间成功 - ID: " + roomId);
        
        return updatedRoom;
    }
    
    /**
     * 删除酒店房间（管理员功能）
     * @param roomId 房间ID
     */
    public void deleteRoom(Long roomId) {
        Optional<HotelRoom> roomOptional = hotelRoomRepository.findById(roomId);
        if (!roomOptional.isPresent()) {
            LoggerUtil.warn(logger, "删除房间失败：房间不存在 - ID: " + roomId);
            throw new BusinessException("房间不存在");
        }
        
        hotelRoomRepository.deleteById(roomId);
        LoggerUtil.info(logger, "删除房间成功 - ID: " + roomId);
    }
}
