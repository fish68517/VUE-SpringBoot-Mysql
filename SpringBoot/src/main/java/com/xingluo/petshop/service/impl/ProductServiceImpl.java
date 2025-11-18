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
}
