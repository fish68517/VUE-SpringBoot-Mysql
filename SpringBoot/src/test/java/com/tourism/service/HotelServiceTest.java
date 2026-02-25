package com.tourism.service;

import com.tourism.entity.Hotel;
import com.tourism.entity.HotelRoom;
import com.tourism.exception.BusinessException;
import com.tourism.repository.HotelRepository;
import com.tourism.repository.HotelRoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 酒店服务单元测试
 */
@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    
    @Mock
    private HotelRepository hotelRepository;
    
    @Mock
    private HotelRoomRepository hotelRoomRepository;
    
    @InjectMocks
    private HotelService hotelService;
    
    @Test
    public void testGetHotelListSuccess() {
        // 准备测试数据
        Hotel hotel1 = new Hotel();
        hotel1.setId(1L);
        hotel1.setName("广州白天鹅宾馆");
        hotel1.setRating(new BigDecimal("5.0"));
        
        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("广州香格里拉大酒店");
        hotel2.setRating(new BigDecimal("4.8"));
        
        List<Hotel> hotels = Arrays.asList(hotel1, hotel2);
        Page<Hotel> mockPage = new PageImpl<>(hotels, PageRequest.of(0, 10), 2);
        
        when(hotelRepository.findAll(PageRequest.of(0, 10))).thenReturn(mockPage);
        
        // 执行获取酒店列表
        Page<Hotel> result = hotelService.getHotelList(0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        
        // 验证findAll方法被调用
        verify(hotelRepository, times(1)).findAll(PageRequest.of(0, 10));
    }
    
    @Test
    public void testSearchHotelsSuccess() {
        // 准备测试数据
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("广州白天鹅宾馆");
        
        List<Hotel> hotels = Arrays.asList(hotel);
        Page<Hotel> mockPage = new PageImpl<>(hotels, PageRequest.of(0, 10), 1);
        
        when(hotelRepository.searchByKeyword("白天鹅", PageRequest.of(0, 10)))
            .thenReturn(mockPage);
        
        // 执行搜索酒店
        Page<Hotel> result = hotelService.searchHotels("白天鹅", 0, 10);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("广州白天鹅宾馆", result.getContent().get(0).getName());
        
        // 验证searchByKeyword方法被调用
        verify(hotelRepository, times(1)).searchByKeyword("白天鹅", PageRequest.of(0, 10));
    }
    
    @Test
    public void testGetHotelDetailSuccess() {
        // 准备测试数据
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("广州白天鹅宾馆");
        hotel.setLocation("广州市越秀区环市东路");
        
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        
        // 执行获取酒店详情
        Hotel result = hotelService.getHotelDetail(1L);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("广州白天鹅宾馆", result.getName());
        
        // 验证findById方法被调用
        verify(hotelRepository, times(1)).findById(1L);
    }
    
    @Test
    public void testGetHotelDetailNotFound() {
        // 设置酒店不存在
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            hotelService.getHotelDetail(999L);
        });
    }
    
    @Test
    public void testGetHotelRoomsSuccess() {
        // 准备测试数据
        Long hotelId = 1L;
        
        HotelRoom room1 = new HotelRoom();
        room1.setId(1L);
        room1.setHotelId(hotelId);
        room1.setRoomType("豪华套房");
        room1.setPricePerNight(new BigDecimal("1500.00"));
        
        HotelRoom room2 = new HotelRoom();
        room2.setId(2L);
        room2.setHotelId(hotelId);
        room2.setRoomType("标准间");
        room2.setPricePerNight(new BigDecimal("800.00"));
        
        when(hotelRoomRepository.findByHotelId(hotelId))
            .thenReturn(Arrays.asList(room1, room2));
        
        // 执行获取酒店房间
        List<HotelRoom> result = hotelService.getHotelRooms(hotelId);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("豪华套房", result.get(0).getRoomType());
        assertEquals("标准间", result.get(1).getRoomType());
        
        // 验证findByHotelId方法被调用
        verify(hotelRoomRepository, times(1)).findByHotelId(hotelId);
    }
    
    @Test
    public void testCreateHotelSuccess() {
        // 准备测试数据
        String name = "新酒店";
        String location = "广州市";
        String description = "新酒店描述";
        String imageUrl = "http://example.com/image.jpg";
        BigDecimal rating = new BigDecimal("4.5");
        
        when(hotelRepository.existsByName(name)).thenReturn(false);
        
        Hotel mockHotel = new Hotel();
        mockHotel.setId(1L);
        mockHotel.setName(name);
        mockHotel.setLocation(location);
        mockHotel.setDescription(description);
        mockHotel.setImageUrl(imageUrl);
        mockHotel.setRating(rating);
        
        when(hotelRepository.save(any(Hotel.class))).thenReturn(mockHotel);
        
        // 执行创建酒店
        Hotel result = hotelService.createHotel(name, location, description, imageUrl, rating);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(location, result.getLocation());
        
        // 验证save方法被调用
        verify(hotelRepository, times(1)).save(any(Hotel.class));
    }
    
    @Test
    public void testCreateHotelWithDuplicateName() {
        // 设置酒店名称已存在
        when(hotelRepository.existsByName("已存在的酒店")).thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            hotelService.createHotel("已存在的酒店", "广州市", "描述", "url", new BigDecimal("4.5"));
        });
        
        // 验证save方法未被调用
        verify(hotelRepository, never()).save(any(Hotel.class));
    }
    
    @Test
    public void testAddRoomSuccess() {
        // 准备测试数据
        Long hotelId = 1L;
        String roomType = "豪华套房";
        BigDecimal pricePerNight = new BigDecimal("1500.00");
        Integer quantity = 10;
        
        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(hotelRoomRepository.existsByHotelIdAndRoomType(hotelId, roomType))
            .thenReturn(false);
        
        HotelRoom mockRoom = new HotelRoom();
        mockRoom.setId(1L);
        mockRoom.setHotelId(hotelId);
        mockRoom.setRoomType(roomType);
        mockRoom.setPricePerNight(pricePerNight);
        mockRoom.setQuantity(quantity);
        
        when(hotelRoomRepository.save(any(HotelRoom.class))).thenReturn(mockRoom);
        
        // 执行添加房间
        HotelRoom result = hotelService.addRoom(hotelId, roomType, pricePerNight, quantity);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(roomType, result.getRoomType());
        assertEquals(pricePerNight, result.getPricePerNight());
        
        // 验证save方法被调用
        verify(hotelRoomRepository, times(1)).save(any(HotelRoom.class));
    }
    
    @Test
    public void testAddRoomWithNonexistentHotel() {
        // 设置酒店不存在
        when(hotelRepository.existsById(999L)).thenReturn(false);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            hotelService.addRoom(999L, "豪华套房", new BigDecimal("1500.00"), 10);
        });
        
        // 验证save方法未被调用
        verify(hotelRoomRepository, never()).save(any(HotelRoom.class));
    }
    
    @Test
    public void testAddRoomWithDuplicateRoomType() {
        // 准备测试数据
        Long hotelId = 1L;
        String roomType = "豪华套房";
        
        when(hotelRepository.existsById(hotelId)).thenReturn(true);
        when(hotelRoomRepository.existsByHotelIdAndRoomType(hotelId, roomType))
            .thenReturn(true);
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            hotelService.addRoom(hotelId, roomType, new BigDecimal("1500.00"), 10);
        });
        
        // 验证save方法未被调用
        verify(hotelRoomRepository, never()).save(any(HotelRoom.class));
    }
    
    @Test
    public void testUpdateRoomSuccess() {
        // 准备测试数据
        Long roomId = 1L;
        BigDecimal newPrice = new BigDecimal("1600.00");
        Integer newQuantity = 15;
        
        HotelRoom existingRoom = new HotelRoom();
        existingRoom.setId(roomId);
        existingRoom.setRoomType("豪华套房");
        existingRoom.setPricePerNight(new BigDecimal("1500.00"));
        existingRoom.setQuantity(10);
        
        when(hotelRoomRepository.findById(roomId)).thenReturn(Optional.of(existingRoom));
        when(hotelRoomRepository.save(any(HotelRoom.class))).thenReturn(existingRoom);
        
        // 执行更新房间
        HotelRoom result = hotelService.updateRoom(roomId, newPrice, newQuantity);
        
        // 验证结果
        assertNotNull(result);
        
        // 验证save方法被调用
        verify(hotelRoomRepository, times(1)).save(any(HotelRoom.class));
    }
    
    @Test
    public void testDeleteRoomSuccess() {
        // 准备测试数据
        Long roomId = 1L;
        
        HotelRoom room = new HotelRoom();
        room.setId(roomId);
        
        when(hotelRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
        
        // 执行删除房间
        hotelService.deleteRoom(roomId);
        
        // 验证deleteById方法被调用
        verify(hotelRoomRepository, times(1)).deleteById(roomId);
    }
    
    @Test
    public void testDeleteRoomNotFound() {
        // 设置房间不存在
        when(hotelRoomRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 验证抛出异常
        assertThrows(BusinessException.class, () -> {
            hotelService.deleteRoom(999L);
        });
        
        // 验证deleteById方法未被调用
        verify(hotelRoomRepository, never()).deleteById(any());
    }
}
