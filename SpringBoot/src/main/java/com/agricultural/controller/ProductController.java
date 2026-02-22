package com.agricultural.controller;

import com.agricultural.dto.Result;
import com.agricultural.entity.AgriculturalProduct;
import com.agricultural.service.ProductService;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 农资产品控制层
 * 
 * 处理农资产品相关的HTTP请求，包括添加、查询、更新、删除等操作
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 添加农资产品接口
     * 
     * @param productName 产品名称
     * @param category 产品类别
     * @param description 产品描述
     * @param price 产品价格
     * @param stock 库存数量
     * @param merchantId 商家ID
     * @param applicableWeather 适用天气
     * @return 添加成功的产品信息
     */
    @PostMapping
    public Result<AgriculturalProduct> addProduct(
            @RequestParam @NotBlank(message = "产品名称不能为空") String productName,
            @RequestParam @NotBlank(message = "产品类别不能为空") String category,
            @RequestParam(required = false) String description,
            @RequestParam @NotNull(message = "产品价格不能为空") BigDecimal price,
            @RequestParam @NotNull(message = "库存数量不能为空") Integer stock,
            @RequestParam @NotNull(message = "商家ID不能为空") Long merchantId,
            @RequestParam(required = false) String applicableWeather) {
        
        LoggerUtil.info("收到添加农资产品请求，产品名称: {}, 商家ID: {}", productName, merchantId);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(productName)) {
                LoggerUtil.warn("添加产品请求参数验证失败: 产品名称为空");
                return Result.validationError("产品名称不能为空");
            }
            
            if (StringUtil.isBlank(category)) {
                LoggerUtil.warn("添加产品请求参数验证失败: 产品类别为空");
                return Result.validationError("产品类别不能为空");
            }
            
            if (price == null) {
                LoggerUtil.warn("添加产品请求参数验证失败: 产品价格为空");
                return Result.validationError("产品价格不能为空");
            }
            
            if (stock == null) {
                LoggerUtil.warn("添加产品请求参数验证失败: 库存数量为空");
                return Result.validationError("库存数量不能为空");
            }
            
            if (merchantId == null) {
                LoggerUtil.warn("添加产品请求参数验证失败: 商家ID为空");
                return Result.validationError("商家ID不能为空");
            }
            
            // 调用业务层添加产品
            AgriculturalProduct addedProduct = productService.addProduct(
                    productName, category, description, price, stock, merchantId, applicableWeather);
            
            LoggerUtil.info("添加农资产品成功，产品ID: {}, 产品名称: {}", addedProduct.getId(), productName);
            return Result.success("产品添加成功", addedProduct);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("添加产品失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("添加产品异常: " + e.getMessage(), e);
            return Result.error("添加产品失败，请稍后重试");
        }
    }

    /**
     * 获取产品列表接口
     * 
     * @return 所有产品列表
     */
    @GetMapping
    public Result<List<AgriculturalProduct>> getProductList() {
        
        LoggerUtil.info("收到获取产品列表请求");
        
        try {
            // 调用业务层获取产品列表
            List<AgriculturalProduct> products = productService.getProductList();
            
            LoggerUtil.info("获取产品列表成功，产品总数: {}", products.size());
            return Result.success(products);
            
        } catch (Exception e) {
            LoggerUtil.error("获取产品列表异常: " + e.getMessage(), e);
            return Result.error("获取产品列表失败，请稍后重试");
        }
    }

    /**
     * 获取产品详情接口
     * 
     * @param id 产品ID
     * @return 产品详情
     */
    @GetMapping("/{id}")
    public Result<AgriculturalProduct> getProductById(
            @PathVariable @NotNull(message = "产品ID不能为空") Long id) {
        
        LoggerUtil.info("收到获取产品详情请求，产品ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("获取产品详情请求参数验证失败: 产品ID无效");
                return Result.validationError("产品ID无效");
            }
            
            // 调用业务层获取产品详情
            AgriculturalProduct product = productService.getProductById(id);
            
            LoggerUtil.info("获取产品详情成功，产品ID: {}", id);
            return Result.success(product);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("获取产品详情失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("获取产品详情异常: " + e.getMessage(), e);
            return Result.error("获取产品详情失败，请稍后重试");
        }
    }

    /**
     * 按类别查询产品接口
     * 
     * @param category 产品类别
     * @return 指定类别的产品列表
     */
    @GetMapping("/category/{category}")
    public Result<List<AgriculturalProduct>> getProductsByCategory(
            @PathVariable @NotBlank(message = "产品类别不能为空") String category) {
        
        LoggerUtil.info("收到按类别查询产品请求，类别: {}", category);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(category)) {
                LoggerUtil.warn("按类别查询产品请求参数验证失败: 类别为空");
                return Result.validationError("产品类别不能为空");
            }
            
            // 调用业务层按类别查询产品
            List<AgriculturalProduct> products = productService.getProductsByCategory(category);
            
            LoggerUtil.info("按类别查询产品成功，类别: {}, 产品数: {}", category, products.size());
            return Result.success(products);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按类别查询产品失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按类别查询产品异常: " + e.getMessage(), e);
            return Result.error("按类别查询产品失败，请稍后重试");
        }
    }

    /**
     * 按商家ID查询产品接口
     * 
     * @param merchantId 商家ID
     * @return 指定商家的产品列表
     */
    @GetMapping("/merchant/{merchantId}")
    public Result<List<AgriculturalProduct>> getProductsByMerchant(
            @PathVariable @NotNull(message = "商家ID不能为空") Long merchantId) {
        
        LoggerUtil.info("收到按商家ID查询产品请求，商家ID: {}", merchantId);
        
        try {
            // 参数验证
            if (merchantId == null || merchantId <= 0) {
                LoggerUtil.warn("按商家ID查询产品请求参数验证失败: 商家ID无效");
                return Result.validationError("商家ID无效");
            }
            
            // 调用业务层按商家ID查询产品
            List<AgriculturalProduct> products = productService.getProductsByMerchant(merchantId);
            
            LoggerUtil.info("按商家ID查询产品成功，商家ID: {}, 产品数: {}", merchantId, products.size());
            return Result.success(products);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按商家ID查询产品失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按商家ID查询产品异常: " + e.getMessage(), e);
            return Result.error("按商家ID查询产品失败，请稍后重试");
        }
    }

    /**
     * 按产品名称模糊查询产品接口
     * 
     * @param productName 产品名称关键词
     * @return 匹配的产品列表
     */
    @GetMapping("/search")
    public Result<List<AgriculturalProduct>> searchProductsByName(
            @RequestParam @NotBlank(message = "产品名称关键词不能为空") String productName) {
        
        LoggerUtil.info("收到按产品名称模糊查询请求，关键词: {}", productName);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(productName)) {
                LoggerUtil.warn("按产品名称模糊查询请求参数验证失败: 关键词为空");
                return Result.validationError("产品名称关键词不能为空");
            }
            
            // 调用业务层按产品名称模糊查询
            List<AgriculturalProduct> products = productService.searchProductsByName(productName);
            
            LoggerUtil.info("按产品名称模糊查询成功，关键词: {}, 产品数: {}", productName, products.size());
            return Result.success(products);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按产品名称模糊查询失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按产品名称模糊查询异常: " + e.getMessage(), e);
            return Result.error("按产品名称模糊查询失败，请稍后重试");
        }
    }

    /**
     * 获取库存充足的产品列表接口
     * 
     * @return 库存充足的产品列表
     */
    @GetMapping("/in-stock")
    public Result<List<AgriculturalProduct>> getProductsInStock() {
        
        LoggerUtil.info("收到获取库存充足产品列表请求");
        
        try {
            // 调用业务层获取库存充足的产品列表
            List<AgriculturalProduct> products = productService.getProductsInStock();
            
            LoggerUtil.info("获取库存充足产品列表成功，产品数: {}", products.size());
            return Result.success(products);
            
        } catch (Exception e) {
            LoggerUtil.error("获取库存充足产品列表异常: " + e.getMessage(), e);
            return Result.error("获取库存充足产品列表失败，请稍后重试");
        }
    }

    /**
     * 按适用天气查询产品接口
     * 
     * @param weather 天气类型
     * @return 适用于指定天气的产品列表
     */
    @GetMapping("/weather/{weather}")
    public Result<List<AgriculturalProduct>> getProductsByApplicableWeather(
            @PathVariable @NotBlank(message = "天气类型不能为空") String weather) {
        
        LoggerUtil.info("收到按适用天气查询产品请求，天气: {}", weather);
        
        try {
            // 参数验证
            if (StringUtil.isBlank(weather)) {
                LoggerUtil.warn("按适用天气查询产品请求参数验证失败: 天气类型为空");
                return Result.validationError("天气类型不能为空");
            }
            
            // 调用业务层按适用天气查询产品
            List<AgriculturalProduct> products = productService.getProductsByApplicableWeather(weather);
            
            LoggerUtil.info("按适用天气查询产品成功，天气: {}, 产品数: {}", weather, products.size());
            return Result.success(products);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("按适用天气查询产品失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("按适用天气查询产品异常: " + e.getMessage(), e);
            return Result.error("按适用天气查询产品失败，请稍后重试");
        }
    }

    /**
     * 更新产品信息接口
     * 
     * @param id 产品ID
     * @param productName 产品名称
     * @param description 产品描述
     * @param price 产品价格
     * @param applicableWeather 适用天气
     * @return 更新后的产品信息
     */
    @PutMapping("/{id}")
    public Result<AgriculturalProduct> updateProduct(
            @PathVariable @NotNull(message = "产品ID不能为空") Long id,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) String applicableWeather) {
        
        LoggerUtil.info("收到更新产品信息请求，产品ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新产品信息请求参数验证失败: 产品ID无效");
                return Result.validationError("产品ID无效");
            }
            
            // 检查是否至少提供了一个要更新的字段
            if (StringUtil.isBlank(productName) && StringUtil.isBlank(description) && 
                price == null && StringUtil.isBlank(applicableWeather)) {
                LoggerUtil.warn("更新产品信息请求参数验证失败: 没有提供要更新的字段");
                return Result.validationError("至少需要提供一个要更新的字段");
            }
            
            // 调用业务层更新产品信息
            AgriculturalProduct updatedProduct = productService.updateProduct(
                    id, productName, description, price, applicableWeather);
            
            LoggerUtil.info("更新产品信息成功，产品ID: {}", id);
            return Result.success("产品信息更新成功", updatedProduct);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新产品信息失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新产品信息异常: " + e.getMessage(), e);
            return Result.error("更新产品信息失败，请稍后重试");
        }
    }

    /**
     * 更新产品库存接口
     * 
     * @param id 产品ID
     * @param quantity 库存变化数量（正数表示增加，负数表示减少）
     * @return 更新后的产品信息
     */
    @PutMapping("/{id}/stock")
    public Result<AgriculturalProduct> updateProductStock(
            @PathVariable @NotNull(message = "产品ID不能为空") Long id,
            @RequestParam @NotNull(message = "库存变化数量不能为空") Integer quantity) {
        
        LoggerUtil.info("收到更新产品库存请求，产品ID: {}, 库存变化: {}", id, quantity);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("更新产品库存请求参数验证失败: 产品ID无效");
                return Result.validationError("产品ID无效");
            }
            
            if (quantity == null) {
                LoggerUtil.warn("更新产品库存请求参数验证失败: 库存变化数量为空");
                return Result.validationError("库存变化数量不能为空");
            }
            
            // 调用业务层更新产品库存
            AgriculturalProduct updatedProduct = productService.updateProductStock(id, quantity);
            
            LoggerUtil.info("更新产品库存成功，产品ID: {}, 新库存: {}", id, updatedProduct.getStock());
            return Result.success("产品库存更新成功", updatedProduct);
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("更新产品库存失败: {}", e.getMessage());
            return Result.validationError(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("更新产品库存异常: " + e.getMessage(), e);
            return Result.error("更新产品库存失败，请稍后重试");
        }
    }

    /**
     * 删除产品接口
     * 
     * @param id 产品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(
            @PathVariable @NotNull(message = "产品ID不能为空") Long id) {
        
        LoggerUtil.info("收到删除产品请求，产品ID: {}", id);
        
        try {
            // 参数验证
            if (id == null || id <= 0) {
                LoggerUtil.warn("删除产品请求参数验证失败: 产品ID无效");
                return Result.validationError("产品ID无效");
            }
            
            // 调用业务层删除产品
            productService.deleteProduct(id);
            
            LoggerUtil.info("删除产品成功，产品ID: {}", id);
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            LoggerUtil.warn("删除产品失败: {}", e.getMessage());
            return Result.notFound(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error("删除产品异常: " + e.getMessage(), e);
            return Result.error("删除产品失败，请稍后重试");
        }
    }

    /**
     * 获取库存不足的产品列表接口
     * 
     * @return 库存不足的产品列表
     */
    @GetMapping("/low-stock")
    public Result<List<AgriculturalProduct>> getLowStockProducts() {
        
        LoggerUtil.info("收到获取库存不足产品列表请求");
        
        try {
            // 调用业务层获取库存不足的产品列表
            List<AgriculturalProduct> products = productService.getLowStockProducts();
            
            LoggerUtil.info("获取库存不足产品列表成功，产品数: {}", products.size());
            return Result.success(products);
            
        } catch (Exception e) {
            LoggerUtil.error("获取库存不足产品列表异常: " + e.getMessage(), e);
            return Result.error("获取库存不足产品列表失败，请稍后重试");
        }
    }

    /**
     * 获取产品总数接口
     * 
     * @return 产品总数
     */
    @GetMapping("/count")
    public Result<Long> getProductCount() {
        
        LoggerUtil.info("收到获取产品总数请求");
        
        try {
            // 调用业务层获取产品总数
            long count = productService.getProductCount();
            
            LoggerUtil.info("获取产品总数成功，产品总数: {}", count);
            return Result.success(count);
            
        } catch (Exception e) {
            LoggerUtil.error("获取产品总数异常: " + e.getMessage(), e);
            return Result.error("获取产品总数失败，请稍后重试");
        }
    }
}
