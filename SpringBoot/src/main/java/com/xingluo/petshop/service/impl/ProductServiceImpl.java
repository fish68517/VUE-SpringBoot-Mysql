package com.xingluo.petshop.service.impl;

import com.xingluo.petshop.entity.Category;
import com.xingluo.petshop.entity.Pet;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.repository.CategoryRepository;
import com.xingluo.petshop.repository.ProductRepository;
import com.xingluo.petshop.service.BrowseHistoryService;
import com.xingluo.petshop.service.PetService;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final PetService petService;
    private final CategoryRepository categoryRepository;
    private final BrowseHistoryService browseHistoryService;
    
    // 商品状态：1-上架
    private static final Integer STATUS_ON_SALE = 1;
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductList(Pageable pageable) {
        // 只返回上架状态的商品
        return productRepository.findByStatus(STATUS_ON_SALE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        // 只返回上架状态的商品
        return productRepository.findByCategoryIdAndStatus(categoryId, STATUS_ON_SALE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        // 只搜索上架状态的商品
        return productRepository.searchProducts(keyword, STATUS_ON_SALE, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> recommendByPetSpecies(Long userId, int limit) {
        // 获取用户的宠物档案
        List<Pet> pets = petService.getPetsByUserId(userId);
        
        if (pets.isEmpty()) {
            // 如果用户没有宠物档案，返回热门商品
            return getHotProducts(limit);
        }
        
        // 获取宠物种类
        Set<String> species = pets.stream()
                .map(Pet::getSpecies)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toSet());
        
        if (species.isEmpty()) {
            return getHotProducts(limit);
        }
        
        // 根据宠物种类查找相关分类
        List<Long> categoryIds = new ArrayList<>();
        for (String petSpecies : species) {
            List<Category> categories = categoryRepository.findByNameContaining(petSpecies);
            categoryIds.addAll(categories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()));
        }
        
        if (categoryIds.isEmpty()) {
            return getHotProducts(limit);
        }
        
        // 根据分类查询商品，按销量排序
        return productRepository.findByCategoryIdInAndStatusOrderBySalesDesc(
                categoryIds, STATUS_ON_SALE, PageRequest.of(0, limit));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> recommendByBrowseHistory(Long userId, int limit) {
        // 获取用户最近浏览的商品ID
        List<Long> browsedProductIds = browseHistoryService.getRecentBrowsedProductIds(userId, 10);
        
        if (browsedProductIds.isEmpty()) {
            // 如果没有浏览历史，返回热门商品
            return getHotProducts(limit);
        }
        
        // 查找相同分类的其他热门商品
        return productRepository.findSimilarProductsByIds(
                browsedProductIds, STATUS_ON_SALE, PageRequest.of(0, limit));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> getRecommendedProducts(Long userId, int limit) {
        Set<Product> recommendedProducts = new LinkedHashSet<>();
        
        // 1. 基于宠物种类的推荐（占50%）
        List<Product> petBasedRecommendations = recommendByPetSpecies(userId, limit / 2);
        recommendedProducts.addAll(petBasedRecommendations);
        
        // 2. 基于浏览历史的推荐（占50%）
        List<Product> historyBasedRecommendations = recommendByBrowseHistory(userId, limit / 2);
        recommendedProducts.addAll(historyBasedRecommendations);
        
        // 3. 如果推荐数量不足，补充热门商品
        if (recommendedProducts.size() < limit) {
            List<Product> hotProducts = getHotProducts(limit - recommendedProducts.size());
            recommendedProducts.addAll(hotProducts);
        }
        
        // 返回指定数量的推荐商品
        return recommendedProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取热门商品（按销量排序）
     * @param limit 数量限制
     * @return 热门商品列表
     */
    private List<Product> getHotProducts(int limit) {
        Page<Product> hotProducts = productRepository.findByStatus(
                STATUS_ON_SALE, PageRequest.of(0, limit));
        return hotProducts.getContent();
    }
    
    @Override
    public Product createProduct(Product product) {
        // 设置商品状态为待审核
        product.setStatus(2);
        product.setSales(0);
        return productRepository.save(product);
    }
    
    @Override
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品是否属于该店铺
        if (!existingProduct.getShopId().equals(product.getShopId())) {
            throw new RuntimeException("无权限修改该商品");
        }
        
        // 更新商品信息
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStock(product.getStock());
        existingProduct.setImage(product.getImage());
        existingProduct.setImages(product.getImages());
        existingProduct.setCategoryId(product.getCategoryId());
        
        return productRepository.save(existingProduct);
    }
    
    @Override
    public void deleteProduct(Long id, Long shopId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品是否属于该店铺
        if (!product.getShopId().equals(shopId)) {
            throw new RuntimeException("无权限删除该商品");
        }
        
        productRepository.delete(product);
    }
    
    @Override
    public void updateProductStatus(Long id, Long shopId, Integer status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品是否属于该店铺
        if (!product.getShopId().equals(shopId)) {
            throw new RuntimeException("无权限修改该商品状态");
        }
        
        // 验证状态值（0-下架/1-上架）
        if (status != 0 && status != 1) {
            throw new RuntimeException("无效的商品状态");
        }
        
        product.setStatus(status);
        productRepository.save(product);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByShop(Long shopId, Pageable pageable) {
        return productRepository.findByShopId(shopId, pageable);
    }
    
    @Override
    public Product updateStock(Long id, Long shopId, Integer stock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品是否属于该店铺
        if (!product.getShopId().equals(shopId)) {
            throw new RuntimeException("无权限修改该商品库存");
        }
        
        // 验证库存值
        if (stock < 0) {
            throw new RuntimeException("库存数量不能为负数");
        }
        
        // 更新库存
        product.setStock(stock);
        
        // 库存为零时自动标记为下架状态
        if (stock == 0 && product.getStatus() == 1) {
            product.setStatus(0);
        }
        
        return productRepository.save(product);
    }
    
    @Override
    public void deductStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证库存是否充足
        if (product.getStock() < quantity) {
            throw new RuntimeException("商品 " + product.getName() + " 库存不足");
        }
        
        // 扣减库存
        product.setStock(product.getStock() - quantity);
        
        // 库存为零时自动标记为下架状态
        if (product.getStock() == 0 && product.getStatus() == 1) {
            product.setStatus(0);
        }
        
        productRepository.save(product);
    }
    
    @Override
    public void restoreStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 恢复库存
        product.setStock(product.getStock() + quantity);
        
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getPendingAuditProducts(Pageable pageable) {
       /* if (status != null) {
            return productRepository.findByStatus(status, pageable);
        } else {
            return productRepository.findAll(pageable);
        }*/
        return productRepository.findAll(pageable);
    }

    @Override
    public void auditProduct(Long id, Boolean approved, String reason) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        // 验证商品是否处于待审核状态
        if (product.getStatus() != 2) {
            throw new RuntimeException("该商品不是待审核状态");
        }
        
        if (approved) {
            // 审核通过，设置状态为已审核（可上架）
            product.setStatus(1);
        } else {
            // 审核拒绝，设置状态为已拒绝
            product.setStatus(3);
            // 可以在这里记录拒绝原因，如果需要的话可以添加一个字段
        }
        
        productRepository.save(product);
    }
}
