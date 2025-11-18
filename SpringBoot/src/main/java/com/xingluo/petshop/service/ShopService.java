package com.xingluo.petshop.service;

import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 店铺服务接口
 */
public interface ShopService {
    
    /**
     * 创建店铺
     */
    Shop createShop(Shop shop);
    
    /**
     * 根据ID查询店铺信息
     */
    Shop getShopById(Long id);
    
    /**
     * 根据用户ID查询店铺信息
     */
    Shop getShopByUserId(Long userId);
    
    /**
     * 更新店铺信息
     */
    Shop updateShop(Long id, Shop shop);
    
    /**
     * 查询店铺商品列表
     */
    List<Product> getShopProducts(Long shopId);
    
    /**
     * 查询所有店铺列表（分页）- 管理员功能
     */
    Page<Shop> getAllShops(Pageable pageable);
    
    /**
     * 审核店铺 - 管理员功能
     * @param id 店铺ID
     * @param status 审核状态 (0待审核/1正常/2禁用)
     */
    Shop auditShop(Long id, Integer status);
    
    /**
     * 启用/禁用店铺 - 管理员功能
     * @param id 店铺ID
     * @param status 状态 (1正常/2禁用)
     */
    Shop updateShopStatus(Long id, Integer status);
}
