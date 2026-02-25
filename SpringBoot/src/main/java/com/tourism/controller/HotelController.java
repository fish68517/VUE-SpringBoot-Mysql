package com.tourism.controller;

import com.tourism.common.ApiResponse;
import com.tourism.entity.Hotel;
import com.tourism.entity.HotelRoom;
import com.tourism.service.HotelService;
import com.tourism.util.LoggerUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 酒店管理控制器
 */
@RestController
@RequestMapping("/hotels")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HotelController {
    
    private static final Logger logger = LoggerUtil.getLogger(HotelController.class);
    
    @Autowired
    private HotelService hotelService;
    
    /**
     * 获取酒店列表端点（分页）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getHotelList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理获取酒店列表请求 - 页码: " + page + ", 每页数量: " + size);
            
            Page<Hotel> hotelPage = hotelService.getHotelList(page, size);
            
            // 为每个酒店添加房间信息
            List<Map<String, Object>> hotels = hotelPage.getContent().stream()
                .map(this::buildHotelResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("hotels", hotels);
            response.put("total", hotelPage.getTotalElements());
            response.put("currentPage", hotelPage.getNumber());
            response.put("pageSize", hotelPage.getSize());
            response.put("totalPages", hotelPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取酒店列表失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 搜索酒店端点（分页）
     * @param keyword 搜索关键词
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchHotels(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理搜索酒店请求 - 关键词: " + keyword + ", 页码: " + page);
            
            Page<Hotel> hotelPage = hotelService.searchHotels(keyword, page, size);
            
            // 为每个酒店添加房间信息
            List<Map<String, Object>> hotels = hotelPage.getContent().stream()
                .map(this::buildHotelResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("hotels", hotels);
            response.put("total", hotelPage.getTotalElements());
            response.put("currentPage", hotelPage.getNumber());
            response.put("pageSize", hotelPage.getSize());
            response.put("totalPages", hotelPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "搜索酒店失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 按位置查询酒店端点（分页）
     * @param location 位置
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return API响应
     */
    @GetMapping("/by-location")
    public ApiResponse<Map<String, Object>> getHotelsByLocation(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            LoggerUtil.info(logger, "处理按位置查询酒店请求 - 位置: " + location + ", 页码: " + page);
            
            Page<Hotel> hotelPage = hotelService.getHotelsByLocation(location, page, size);
            
            // 为每个酒店添加房间信息
            List<Map<String, Object>> hotels = hotelPage.getContent().stream()
                .map(this::buildHotelResponse)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("hotels", hotels);
            response.put("total", hotelPage.getTotalElements());
            response.put("currentPage", hotelPage.getNumber());
            response.put("pageSize", hotelPage.getSize());
            response.put("totalPages", hotelPage.getTotalPages());
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "按位置查询酒店失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 获取酒店详情端点
     * @param id 酒店ID
     * @return API响应
     */
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getHotelDetail(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理获取酒店详情请求 - ID: " + id);
            
            Hotel hotel = hotelService.getHotelDetail(id);
            Map<String, Object> response = buildHotelResponse(hotel);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "获取酒店详情失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 创建酒店端点（管理员）
     * @param request 创建请求体
     * @return API响应
     */
    @PostMapping("/admin/create")
    public ApiResponse<Map<String, Object>> createHotel(@RequestBody CreateHotelRequest request) {
        try {
            LoggerUtil.info(logger, "处理创建酒店请求 - 酒店名称: " + request.getName());
            
            Hotel hotel = hotelService.createHotel(
                request.getName(),
                request.getLocation(),
                request.getDescription(),
                request.getImageUrl(),
                request.getRating()
            );
            
            Map<String, Object> response = buildHotelResponse(hotel);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "创建酒店失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新酒店端点（管理员）
     * @param id 酒店ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/admin/{id}")
    public ApiResponse<Map<String, Object>> updateHotel(
            @PathVariable Long id,
            @RequestBody UpdateHotelRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新酒店请求 - ID: " + id);
            
            Hotel hotel = hotelService.updateHotel(
                id,
                request.getName(),
                request.getLocation(),
                request.getDescription(),
                request.getImageUrl(),
                request.getRating()
            );
            
            Map<String, Object> response = buildHotelResponse(hotel);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新酒店失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除酒店端点（管理员）
     * @param id 酒店ID
     * @return API响应
     */
    @DeleteMapping("/admin/{id}")
    public ApiResponse<String> deleteHotel(@PathVariable Long id) {
        try {
            LoggerUtil.info(logger, "处理删除酒店请求 - ID: " + id);
            
            hotelService.deleteHotel(id);
            
            return ApiResponse.success("酒店删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除酒店失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 添加酒店房间端点（管理员）
     * @param hotelId 酒店ID
     * @param request 房间请求体
     * @return API响应
     */
    @PostMapping("/admin/{hotelId}/rooms")
    public ApiResponse<Map<String, Object>> addRoom(
            @PathVariable Long hotelId,
            @RequestBody AddRoomRequest request) {
        try {
            LoggerUtil.info(logger, "处理添加房间请求 - 酒店ID: " + hotelId + ", 房间类型: " + request.getRoomType());
            
            HotelRoom room = hotelService.addRoom(
                hotelId,
                request.getRoomType(),
                request.getPricePerNight(),
                request.getQuantity()
            );
            
            Map<String, Object> response = buildRoomResponse(room);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "添加房间失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新酒店房间端点（管理员）
     * @param roomId 房间ID
     * @param request 更新请求体
     * @return API响应
     */
    @PutMapping("/admin/rooms/{roomId}")
    public ApiResponse<Map<String, Object>> updateRoom(
            @PathVariable Long roomId,
            @RequestBody UpdateRoomRequest request) {
        try {
            LoggerUtil.info(logger, "处理更新房间请求 - 房间ID: " + roomId);
            
            HotelRoom room = hotelService.updateRoom(
                roomId,
                request.getPricePerNight(),
                request.getQuantity()
            );
            
            Map<String, Object> response = buildRoomResponse(room);
            return ApiResponse.success(response);
        } catch (Exception e) {
            LoggerUtil.error(logger, "更新房间失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除酒店房间端点（管理员）
     * @param roomId 房间ID
     * @return API响应
     */
    @DeleteMapping("/admin/rooms/{roomId}")
    public ApiResponse<String> deleteRoom(@PathVariable Long roomId) {
        try {
            LoggerUtil.info(logger, "处理删除房间请求 - 房间ID: " + roomId);
            
            hotelService.deleteRoom(roomId);
            
            return ApiResponse.success("房间删除成功");
        } catch (Exception e) {
            LoggerUtil.error(logger, "删除房间失败: " + e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
    
    /**
     * 构建酒店响应对象
     * @param hotel 酒店对象
     * @return 响应Map
     */
    private Map<String, Object> buildHotelResponse(Hotel hotel) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", hotel.getId());
        response.put("name", hotel.getName());
        response.put("location", hotel.getLocation());
        response.put("description", hotel.getDescription());
        response.put("imageUrl", hotel.getImageUrl());
        response.put("rating", hotel.getRating());
        
        // 添加房间信息
        List<HotelRoom> rooms = hotelService.getHotelRooms(hotel.getId());
        List<Map<String, Object>> roomList = rooms.stream()
            .map(this::buildRoomResponse)
            .collect(Collectors.toList());
        response.put("rooms", roomList);
        
        return response;
    }
    
    /**
     * 构建房间响应对象
     * @param room 房间对象
     * @return 响应Map
     */
    private Map<String, Object> buildRoomResponse(HotelRoom room) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", room.getId());
        response.put("hotelId", room.getHotelId());
        response.put("roomType", room.getRoomType());
        response.put("pricePerNight", room.getPricePerNight());
        response.put("quantity", room.getQuantity());
        return response;
    }
    
    /**
     * 创建酒店请求体
     */
    public static class CreateHotelRequest {
        private String name;
        private String location;
        private String description;
        private String imageUrl;
        private BigDecimal rating;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public BigDecimal getRating() { return rating; }
        public void setRating(BigDecimal rating) { this.rating = rating; }
    }
    
    /**
     * 更新酒店请求体
     */
    public static class UpdateHotelRequest {
        private String name;
        private String location;
        private String description;
        private String imageUrl;
        private BigDecimal rating;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public BigDecimal getRating() { return rating; }
        public void setRating(BigDecimal rating) { this.rating = rating; }
    }
    
    /**
     * 添加房间请求体
     */
    public static class AddRoomRequest {
        private String roomType;
        private BigDecimal pricePerNight;
        private Integer quantity;
        
        public String getRoomType() { return roomType; }
        public void setRoomType(String roomType) { this.roomType = roomType; }
        
        public BigDecimal getPricePerNight() { return pricePerNight; }
        public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    /**
     * 更新房间请求体
     */
    public static class UpdateRoomRequest {
        private BigDecimal pricePerNight;
        private Integer quantity;
        
        public BigDecimal getPricePerNight() { return pricePerNight; }
        public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
