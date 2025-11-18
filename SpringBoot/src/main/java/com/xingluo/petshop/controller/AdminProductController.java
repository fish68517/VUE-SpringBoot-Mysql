package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.AuditProductDTO;
import com.xingluo.petshop.dto.ProductVO;
import com.xingluo.petshop.entity.Product;
import com.xingluo.petshop.service.ProductService;
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
 * 管理员商品审核控制器
 * 提供商品审核功能
 */
@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    /**
     * 获取待审核商品列表（分页）
     * GET /api/admin/product/list
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 待审核商品分页列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getPendingAuditProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 创建分页参数，按创建时间倒序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 查询待审核商品列表
        Page<Product> productPage = productService.getPendingAuditProducts(pageable);
        
        // 转换为VO
        Page<ProductVO> productVOPage = productPage.map(this::convertToVO);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", productVOPage.getContent());
        result.put("totalElements", productVOPage.getTotalElements());
        result.put("totalPages", productVOPage.getTotalPages());
        result.put("currentPage", productVOPage.getNumber());
        result.put("pageSize", productVOPage.getSize());
        
        return ApiResponse.ok(result);
    }

    /**
     * 审核商品
     * PUT /api/admin/product/{id}/audit
     * @param id 商品ID
     * @param auditDTO 审核信息
     * @return 操作结果
     */
    @PutMapping("/{id}/audit")
    public ApiResponse<String> auditProduct(
            @PathVariable Long id,
            @RequestBody AuditProductDTO auditDTO) {
        
        // 验证审核信息
        if (auditDTO.getApproved() == null) {
            return ApiResponse.error("请指定审核结果");
        }
        
        // 审核商品
        productService.auditProduct(id, auditDTO.getApproved(), auditDTO.getReason());
        
        String message = auditDTO.getApproved() ? "商品审核通过" : "商品审核拒绝";
        return ApiResponse.ok(message);
    }

    /**
     * 将Product实体转换为ProductVO
     */
    private ProductVO convertToVO(Product product) {
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(product, productVO);
        
        // 设置关联信息
        if (product.getShop() != null) {
            productVO.setShopName(product.getShop().getName());
        }
        if (product.getCategory() != null) {
            productVO.setCategoryName(product.getCategory().getName());
        }
        
        return productVO;
    }
}
