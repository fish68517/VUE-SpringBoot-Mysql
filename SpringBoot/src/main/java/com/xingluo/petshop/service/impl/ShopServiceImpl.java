package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.entity.Shop;
import com.xingluo.petshop.repository.ProductRepository;
import com.xingluo.petshop.repository.ShopRepository;
import com.xingluo.petshop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 店铺服务实现类
 */
@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    
    /**
     * 创建店铺
     */
    @Override
    @Transactional
    public Shop createShop(Shop shop) {
        // 检查用户是否已有店铺
        if (shopRepository.existsByUserId(shop.getUserId())) {
            throw new RuntimeException("该用户已有店铺");
        }
        
        // 设置默认状态为待审核
        if (shop.getStatus() == null) {
            shop.setStatus(0);
        }
        
        return shopRepository.save(shop);
    }
    
    /**
     * 根据ID查询店铺信息
     */
    @Override
    public Shop getShopById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
    }
    
    /**
     * 根据用户ID查询店铺信息
     */
    @Override
    public Shop getShopByUserId(Long userId) {
        return shopRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("该用户没有店铺"));
    }
    
    /**
     * 更新店铺信息
     */
    @Override
    @Transactional
    public Shop updateShop(Long id, Shop shop) {
        // 查询店铺是否存在
        Shop existingShop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        // 更新店铺信息
        if (shop.getName() != null) {
            existingShop.setName(shop.getName());
        }
        if (shop.getDescription() != null) {
            existingShop.setDescription(shop.getDescription());
        }
        if (shop.getLogo() != null) {
            existingShop.setLogo(shop.getLogo());
        }
        if (shop.getContact() != null) {
            existingShop.setContact(shop.getContact());
        }
        
        return shopRepository.save(existingShop);
    }
    
    /**
     * 查询店铺商品列表
     */
    @Override
    public List<Product> getShopProducts(Long shopId) {
        // 验证店铺是否存在
        shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        return productRepository.findByShopId(shopId);
    }
    
    /**
     * 查询所有店铺列表（分页）- 管理员功能
     */
    @Override
    public Page<Shop> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable);
    }
    
    /**
     * 审核店铺 - 管理员功能
     */
    @Override
    @Transactional
    public Shop auditShop(Long id, Integer status) {
        // 查询店铺是否存在
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        // 验证状态值是否合法 (0待审核/1正常/2禁用)
        if (status < 0 || status > 2) {
            throw new RuntimeException("状态值不合法");
        }
        
        // 更新店铺状态
        shop.setStatus(status);
        
        return shopRepository.save(shop);
    }
    
    /**
     * 启用/禁用店铺 - 管理员功能
     */
    @Override
    @Transactional
    public Shop updateShopStatus(Long id, Integer status) {
        // 查询店铺是否存在
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店铺不存在"));
        
        // 验证状态值是否合法 (1正常/2禁用)
        if (status != 1 && status != 2) {
            throw new RuntimeException("状态值不合法，只能设置为1(正常)或2(禁用)");
        }
        
        // 更新店铺状态
        shop.setStatus(status);
        
        return shopRepository.save(shop);
    }
}
