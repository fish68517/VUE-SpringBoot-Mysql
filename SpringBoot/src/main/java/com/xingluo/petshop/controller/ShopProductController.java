package com.xingluo.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper; // 引入 ObjectMapper
import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.CreateProductDTO;
import com.xingluo.petshop.dto.ProductVO;
import com.xingluo.petshop.dto.UpdateProductDTO;
import com.xingluo.petshop.dto.UpdateProductStatusDTO;
import com.xingluo.petshop.dto.UpdateStockDTO;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 店家商品管理 Controller
 */
@RestController
@RequestMapping("/api/shop/product")
@RequiredArgsConstructor
public class ShopProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper; // 注入 Jackson 工具类

    /**
     * 辅助方法：将 Entity 转换为 VO
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
     * 创建商品
     */
    @PostMapping
    public ApiResponse<ProductVO> createProduct(@RequestBody CreateProductDTO dto) {
        Product product = new Product();
        product.setShopId(dto.getShopId());
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());

        // 核心修改：List<String> -> JSON String
        try {
            if (dto.getImages() != null) {
                product.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } else {
                product.setImages("[]");
            }
        } catch (Exception e) {
            product.setImages("[]");
        }

        Product createdProduct = productService.createProduct(product);
        return ApiResponse.ok(convertToVO(createdProduct));
    }

    /**
     * 获取店铺商品列表
     */
    @GetMapping("/list")
    public ApiResponse<Page<ProductVO>> getShopProducts(
            @RequestParam Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> products = productService.getProductsByShop(shopId, pageable);

        Page<ProductVO> productVOs = products.map(this::convertToVO);

        return ApiResponse.ok(productVOs);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public ApiResponse<ProductVO> updateProduct(@PathVariable Long id, @RequestBody UpdateProductDTO dto) {
        Product product = new Product();
        product.setShopId(dto.getShopId());
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setImage(dto.getImage());

        // 核心修改：List<String> -> JSON String
        try {
            if (dto.getImages() != null) {
                product.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } else {
                product.setImages("[]");
            }
        } catch (Exception e) {
            product.setImages("[]");
        }

        Product updatedProduct = productService.updateProduct(id, product);
        return ApiResponse.ok(convertToVO(updatedProduct));
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id, @RequestParam Long shopId) {
        productService.deleteProduct(id, shopId);
        return ApiResponse.ok("商品删除成功");
    }

    /**
     * 上下架商品
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateProductStatus(@PathVariable Long id, @RequestBody UpdateProductStatusDTO dto) {
        productService.updateProductStatus(id, dto.getShopId(), dto.getStatus());
        String message = dto.getStatus() == 1 ? "商品上架成功" : "商品下架成功";
        return ApiResponse.ok(message);
    }

    /**
     * 更新商品库存
     */
    @PutMapping("/{id}/stock")
    public ApiResponse<ProductVO> updateStock(@PathVariable Long id, @RequestBody UpdateStockDTO dto) {
        Product updatedProduct = productService.updateStock(id, dto.getShopId(), dto.getStock());
        return ApiResponse.ok(convertToVO(updatedProduct));
    }
}