package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreateShopDTO;
import com.xingluo.petshop.dto.ShopVO;
import com.xingluo.petshop.dto.UpdateShopDTO;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.entity.Shop;
import com.xingluo.petshop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店铺控制器
 * 提供店铺的创建、查询、更新等RESTful API
 */
@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {
    
    private final ShopService shopService;
    
    /**
     * 创建店铺
     * POST /api/shop
     */
    @PostMapping
    public ApiResponse<ShopVO> createShop(@RequestBody CreateShopDTO createShopDTO) {
        // 将DTO转换为实体
        Shop shop = new Shop();
        shop.setUserId(createShopDTO.getUserId());
        shop.setName(createShopDTO.getName());
        shop.setDescription(createShopDTO.getDescription());
        shop.setLogo(createShopDTO.getLogo());
        shop.setContact(createShopDTO.getContact());
        
        // 调用服务层创建
        Shop createdShop = shopService.createShop(shop);
        
        // 转换为VO返回
        ShopVO shopVO = convertToVO(createdShop);
        return ApiResponse.ok(shopVO);
    }
    
    /**
     * 获取店铺信息
     * GET /api/shop/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<ShopVO> getShopById(@PathVariable Long id) {
        Shop shop = shopService.getShopById(id);
        
        // 转换为VO返回
        ShopVO shopVO = convertToVO(shop);
        return ApiResponse.ok(shopVO);
    }
    
    /**
     * 更新店铺信息
     * PUT /api/shop/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<ShopVO> updateShop(@PathVariable Long id,
                                          @RequestBody UpdateShopDTO updateShopDTO) {
        // 将DTO转换为实体
        Shop shop = new Shop();
        shop.setName(updateShopDTO.getName());
        shop.setDescription(updateShopDTO.getDescription());
        shop.setLogo(updateShopDTO.getLogo());
        shop.setContact(updateShopDTO.getContact());
        
        // 调用服务层更新
        Shop updatedShop = shopService.updateShop(id, shop);
        
        // 转换为VO返回
        ShopVO shopVO = convertToVO(updatedShop);
        return ApiResponse.ok(shopVO);
    }
    
    /**
     * 获取店铺商品列表
     * GET /api/shop/{id}/products
     */
    @GetMapping("/{id}/products")
    public ApiResponse<List<Product>> getShopProducts(@PathVariable Long id) {
        List<Product> products = shopService.getShopProducts(id);
        return ApiResponse.ok(products);
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
