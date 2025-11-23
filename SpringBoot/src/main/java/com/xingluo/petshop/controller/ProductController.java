package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.ProductVO;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.service.BrowseHistoryService;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器（用户端）
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BrowseHistoryService browseHistoryService;

    /**
     * 辅助方法：将 Entity 转换为 VO
     * 避免直接返回 Entity 导致的序列化错误
     */
    private ProductVO convertToVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        if (product.getShop() != null) {
            vo.setShopName(product.getShop().getName());
        }
        if (product.getCategory() != null) {
            vo.setCategoryName(product.getCategory().getName());
        }
        return vo;
    }

    /**
     * 获取商品列表（分页）
     */
    @GetMapping("/list")
    public ApiResponse<Page<ProductVO>> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> products = productService.getProductList(pageable);

        // 核心修改：将 Product 转换为 ProductVO
        Page<ProductVO> productVOs = products.map(this::convertToVO);

        return ApiResponse.ok(productVOs);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductVO> getProductById(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {

        Product product = productService.getProductById(id);

        // 记录浏览历史
        if (userId != null) {
            try {
                browseHistoryService.recordBrowseHistory(userId, id);
            } catch (Exception e) {
                // 忽略记录失败
            }
        }

        return ApiResponse.ok(convertToVO(product));
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public ApiResponse<Page<ProductVO>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Product> products = productService.searchProducts(keyword, pageable);

        // 转换为 VO
        Page<ProductVO> productVOs = products.map(this::convertToVO);
        return ApiResponse.ok(productVOs);
    }

    /**
     * 按分类查询商品
     */
    @GetMapping("/category/{id}")
    public ApiResponse<Page<ProductVO>> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        Page<Product> products = productService.getProductsByCategory(id, pageable);

        // 转换为 VO
        Page<ProductVO> productVOs = products.map(this::convertToVO);
        return ApiResponse.ok(productVOs);
    }
}