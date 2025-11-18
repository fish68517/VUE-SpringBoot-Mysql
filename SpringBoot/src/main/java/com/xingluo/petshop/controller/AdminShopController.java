package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.ShopVO;
import com.xingluo.petshop.entity.Shop;
import com.xingluo.petshop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员店铺管理控制器
 * 提供店铺列表查询、审核、启用/禁用等管理功能
 */
@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class AdminShopController {

    private final ShopService shopService;

    /**
     * 获取店铺列表（分页）
     * GET /api/admin/shop/list
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 店铺分页列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getShopList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 创建分页参数，按创建时间倒序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 查询店铺列表
        Page<Shop> shopPage = shopService.getAllShops(pageable);
        
        // 转换为VO
        Page<ShopVO> shopVOPage = shopPage.map(this::convertToVO);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", shopVOPage.getContent());
        result.put("totalElements", shopVOPage.getTotalElements());
        result.put("totalPages", shopVOPage.getTotalPages());
        result.put("currentPage", shopVOPage.getNumber());
        result.put("pageSize", shopVOPage.getSize());
        
        return ApiResponse.ok(result);
    }

    /**
     * 审核店铺
     * PUT /api/admin/shop/{id}/audit
     * @param id 店铺ID
     * @param status 审核状态（0待审核/1正常/2禁用）
     * @return 操作结果
     */
    @PutMapping("/{id}/audit")
    public ApiResponse<ShopVO> auditShop(
            @PathVariable Long id,
            @RequestParam Integer status) {
        
        Shop shop = shopService.auditShop(id, status);
        
        ShopVO shopVO = convertToVO(shop);
        
        return ApiResponse.ok(shopVO);
    }

    /**
     * 启用/禁用店铺
     * PUT /api/admin/shop/{id}/status
     * @param id 店铺ID
     * @param status 状态（1正常/2禁用）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateShopStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        
        shopService.updateShopStatus(id, status);
        
        String message = status == 1 ? "店铺已启用" : "店铺已禁用";
        return ApiResponse.ok(message);
    }

    /**
     * 将Shop实体转换为ShopVO
     */
    private ShopVO convertToVO(Shop shop) {
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO);
        return shopVO;
    }
}
