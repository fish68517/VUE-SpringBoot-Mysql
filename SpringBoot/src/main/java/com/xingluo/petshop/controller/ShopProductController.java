package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreateProductDTO;
import com.xingluo.petshop.dto.UpdateProductDTO;
import com.xingluo.petshop.dto.UpdateProductStatusDTO;
import com.xingluo.petshop.dto.UpdateStockDTO;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 店家商品管理 Controller
 */
@RestController
@RequestMapping("/api/shop/product")
@RequiredArgsConstructor
public class ShopProductController {
    
    private final ProductService productService;
    
    /**
     * 创建商品
     * POST /api/shop/product
     * @param dto 创建商品DTO
     * @return 创建的商品
     */
    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody CreateProductDTO dto) {
        Product product = new Product();
        product.setShopId(dto.getShopId());
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());
        product.setImages(dto.getImages());
        
        Product createdProduct = productService.createProduct(product);
        return ApiResponse.ok(createdProduct);
    }
    
    /**
     * 获取店铺商品列表
     * GET /api/shop/product/list
     * @param shopId 店铺ID
     * @param page 页码
     * @param size 每页数量
     * @return 商品分页列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<Product>> getShopProducts(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> products = productService.getProductsByShop(shopId, pageable);
        return ApiResponse.ok(products);
    }
    
    /**
     * 更新商品
     * PUT /api/shop/product/{id}
     * @param id 商品ID
     * @param dto 更新商品DTO
     * @return 更新后的商品
     */
    @PutMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable Long id, @RequestBody UpdateProductDTO dto) {
        Product product = new Product();
        product.setShopId(dto.getShopId());
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());
        product.setImages(dto.getImages());
        
        Product updatedProduct = productService.updateProduct(id, product);
        return ApiResponse.ok(updatedProduct);
    }
    
    /**
     * 删除商品
     * DELETE /api/shop/product/{id}
     * @param id 商品ID
     * @param shopId 店铺ID
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id, @RequestParam Long shopId) {
        productService.deleteProduct(id, shopId);
        return ApiResponse.ok("商品删除成功");
    }
    
    /**
     * 上下架商品
     * PUT /api/shop/product/{id}/status
     * @param id 商品ID
     * @param dto 更新状态DTO
     * @return 成功消息
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateProductStatus(@PathVariable Long id, @RequestBody UpdateProductStatusDTO dto) {
        productService.updateProductStatus(id, dto.getShopId(), dto.getStatus());
        String message = dto.getStatus() == 1 ? "商品上架成功" : "商品下架成功";
        return ApiResponse.ok(message);
    }
    
    /**
     * 更新商品库存
     * PUT /api/shop/product/{id}/stock
     * @param id 商品ID
     * @param dto 更新库存DTO
     * @return 更新后的商品
     */
    @PutMapping("/{id}/stock")
    public ApiResponse<Product> updateStock(@PathVariable Long id, @RequestBody UpdateStockDTO dto) {
        Product updatedProduct = productService.updateStock(id, dto.getShopId(), dto.getStock());
        return ApiResponse.ok(updatedProduct);
    }
}
