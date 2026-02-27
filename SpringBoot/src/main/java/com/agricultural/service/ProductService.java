package com.agricultural.service;

import com.agricultural.entity.AgriculturalProduct;
import com.agricultural.repository.AgriculturalProductRepository;
import com.agricultural.util.LoggerUtil;
import com.agricultural.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 农资产品业务逻辑层
 * 处理农资产品相关的业务逻辑，包括添加产品、获取产品列表、更新产品信息、删除产品、库存管理等操作
 */
@Service
@Transactional
public class ProductService {

    @Autowired
    private AgriculturalProductRepository productRepository;

    /**
     * 添加农资产品
     * 创建新的农资产品记录
     *
     * @param productName 产品名称
     * @param category 产品类别
     * @param description 产品描述
     * @param price 产品价格
     * @param stock 库存数量
     * @param merchantId 商家ID
     * @param applicableWeather 适用天气
     * @return 创建的产品对象
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public AgriculturalProduct addProduct(String productName, String category, String description,
                                          BigDecimal price, Integer stock, Long merchantId, String applicableWeather) {
        LoggerUtil.info("开始添加农资产品，产品名称: {}, 类别: {}, 商家ID: {}", productName, category, merchantId);

        // 验证产品名称
        if (StringUtil.isBlank(productName)) {
            LoggerUtil.warn("添加产品失败: 产品名称为空");
            throw new IllegalArgumentException("产品名称不能为空");
        }

        if (productName.length() > 100) {
            LoggerUtil.warn("添加产品失败: 产品名称过长，产品名称: {}", productName);
            throw new IllegalArgumentException("产品名称长度不能超过100个字符");
        }

        // 验证产品类别
        if (StringUtil.isBlank(category)) {
            LoggerUtil.warn("添加产品失败: 产品类别为空");
            throw new IllegalArgumentException("产品类别不能为空");
        }

        // 验证价格
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            LoggerUtil.warn("添加产品失败: 产品价格无效，价格: {}", price);
            throw new IllegalArgumentException("产品价格必须大于0");
        }

        // 验证库存
        if (stock == null || stock < 0) {
            LoggerUtil.warn("添加产品失败: 库存数量无效，库存: {}", stock);
            throw new IllegalArgumentException("库存数量不能为负数");
        }

        // 验证商家ID
        if (merchantId == null || merchantId <= 0) {
            LoggerUtil.warn("添加产品失败: 商家ID无效，商家ID: {}", merchantId);
            throw new IllegalArgumentException("商家ID无效");
        }

        // 创建产品对象
        AgriculturalProduct product = AgriculturalProduct.builder()
                .productName(productName)
                .category(category)
                .description(description)
                .price(price)
                .stock(stock)
                .merchantId(merchantId)
                .applicableWeather(applicableWeather)
                .build();

        // 保存产品
        AgriculturalProduct savedProduct = productRepository.save(product);
        LoggerUtil.info("农资产品添加成功，产品ID: {}, 产品名称: {}, 商家ID: {}", 
                savedProduct.getId(), productName, merchantId);

        return savedProduct;
    }

    /**
     * 获取产品列表
     * 获取所有农资产品信息
     *
     * @return 产品列表
     */
    public List<AgriculturalProduct> getProductList() {
        LoggerUtil.info("获取农资产品列表");

        List<AgriculturalProduct> products = productRepository.findAll();
        LoggerUtil.info("获取农资产品列表成功，产品总数: {}", products.size());

        return products;
    }

    /**
     * 根据产品ID获取产品详情
     * 获取指定ID的产品信息
     *
     * @param productId 产品ID
     * @return 产品对象
     * @throws IllegalArgumentException 当产品不存在时抛出异常
     */
    public AgriculturalProduct getProductById(Long productId) {
        LoggerUtil.info("根据ID获取产品详情，产品ID: {}", productId);

        // 验证产品ID
        if (productId == null || productId <= 0) {
            LoggerUtil.warn("获取产品失败: 产品ID无效");
            throw new IllegalArgumentException("产品ID无效");
        }

        Optional<AgriculturalProduct> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            LoggerUtil.warn("获取产品失败: 产品不存在，产品ID: {}", productId);
            throw new IllegalArgumentException("产品不存在");
        }

        LoggerUtil.info("根据ID获取产品详情成功，产品ID: {}", productId);

        return productOptional.get();
    }

    /**
     * 根据产品类别获取产品列表
     * 获取指定类别的所有产品信息
     *
     * @param category 产品类别
     * @return 产品列表
     * @throws IllegalArgumentException 当类别为空时抛出异常
     */
    public List<AgriculturalProduct> getProductsByCategory(String category) {
        LoggerUtil.info("根据类别获取产品列表，类别: {}", category);

        // 验证类别参数
        if (StringUtil.isBlank(category)) {
            LoggerUtil.warn("获取产品列表失败: 类别为空");
            throw new IllegalArgumentException("产品类别不能为空");
        }

        List<AgriculturalProduct> products = productRepository.findByCategory(category);
        LoggerUtil.info("根据类别获取产品列表成功，类别: {}, 产品数: {}", category, products.size());

        return products;
    }

    /**
     * 根据商家ID获取产品列表
     * 获取指定商家的所有产品信息
     *
     * @param merchantId 商家ID
     * @return 产品列表
     * @throws IllegalArgumentException 当商家ID无效时抛出异常
     */
    public List<AgriculturalProduct> getProductsByMerchant(Long merchantId) {
        LoggerUtil.info("根据商家ID获取产品列表，商家ID: {}", merchantId);

        // 验证商家ID
        if (merchantId == null || merchantId <= 0) {
            LoggerUtil.warn("获取产品列表失败: 商家ID无效");
            throw new IllegalArgumentException("商家ID无效");
        }

        List<AgriculturalProduct> products = productRepository.findByMerchantId(merchantId);
        LoggerUtil.info("根据商家ID获取产品列表成功，商家ID: {}, 产品数: {}", merchantId, products.size());

        return products;
    }

    /**
     * 根据产品名称模糊查询产品列表
     * 获取产品名称包含指定关键词的所有产品
     *
     * @param productName 产品名称关键词
     * @return 产品列表
     * @throws IllegalArgumentException 当关键词为空时抛出异常
     */
    public List<AgriculturalProduct> searchProductsByName(String productName) {
        LoggerUtil.info("根据产品名称模糊查询产品，关键词: {}", productName);

        // 验证关键词
        if (StringUtil.isBlank(productName)) {
            LoggerUtil.warn("查询产品失败: 产品名称关键词为空");
            throw new IllegalArgumentException("产品名称关键词不能为空");
        }

        List<AgriculturalProduct> products = productRepository.findByProductNameContaining(productName);
        LoggerUtil.info("根据产品名称模糊查询成功，关键词: {}, 产品数: {}", productName, products.size());

        return products;
    }

    /**
     * 获取库存充足的产品列表
     * 获取所有库存大于0的产品，按库存数量降序排列
     *
     * @return 库存充足的产品列表
     */
    public List<AgriculturalProduct> getProductsInStock() {
        LoggerUtil.info("获取库存充足的产品列表");

        List<AgriculturalProduct> products = productRepository.findProductsInStock();
        LoggerUtil.info("获取库存充足的产品列表成功，产品数: {}", products.size());

        return products;
    }

    /**
     * 根据适用天气获取产品列表
     * 获取适用于指定天气且库存充足的产品
     *
     * @param weather 天气类型
     * @return 产品列表
     * @throws IllegalArgumentException 当天气类型为空时抛出异常
     */
    public List<AgriculturalProduct> getProductsByApplicableWeather(String weather) {
        LoggerUtil.info("根据适用天气获取产品列表，天气: {}", weather);

        // 验证天气参数
        if (StringUtil.isBlank(weather)) {
            LoggerUtil.warn("获取产品列表失败: 天气类型为空");
            throw new IllegalArgumentException("天气类型不能为空");
        }

        List<AgriculturalProduct> products = productRepository.findByApplicableWeather(weather);
        LoggerUtil.info("根据适用天气获取产品列表成功，天气: {}, 产品数: {}", weather, products.size());

        return products;
    }

    /**
     * 根据类别和商家ID获取产品列表
     * 获取指定商家在指定类别下的所有产品
     *
     * @param category 产品类别
     * @param merchantId 商家ID
     * @return 产品列表
     * @throws IllegalArgumentException 当参数无效时抛出异常
     */
    public List<AgriculturalProduct> getProductsByCategoryAndMerchant(String category, Long merchantId) {
        LoggerUtil.info("根据类别和商家ID获取产品列表，类别: {}, 商家ID: {}", category, merchantId);

        // 验证类别
        if (StringUtil.isBlank(category)) {
            LoggerUtil.warn("获取产品列表失败: 类别为空");
            throw new IllegalArgumentException("产品类别不能为空");
        }

        // 验证商家ID
        if (merchantId == null || merchantId <= 0) {
            LoggerUtil.warn("获取产品列表失败: 商家ID无效");
            throw new IllegalArgumentException("商家ID无效");
        }

        List<AgriculturalProduct> products = productRepository.findByCategoryAndMerchantId(category, merchantId);
        LoggerUtil.info("根据类别和商家ID获取产品列表成功，类别: {}, 商家ID: {}, 产品数: {}", 
                category, merchantId, products.size());

        return products;
    }

    /**
     * 更新产品信息
     * 支持更新产品名称、描述、价格、适用天气等信息
     *
     * @param productId 产品ID
     * @param productName 产品名称
     * @param description 产品描述
     * @param price 产品价格
     * @param applicableWeather 适用天气
     * @return 更新后的产品对象
     * @throws IllegalArgumentException 当产品不存在或参数无效时抛出异常
     */
    public AgriculturalProduct updateProduct(Long productId, String productName, String description,
                                             BigDecimal price, String applicableWeather) {
        LoggerUtil.info("开始更新产品信息，产品ID: {}", productId);

        // 获取产品
        AgriculturalProduct product = getProductById(productId);

        // 验证并更新产品名称
        if (StringUtil.isNotBlank(productName)) {
            if (productName.length() > 100) {
                LoggerUtil.warn("更新产品失败: 产品名称过长，产品ID: {}", productId);
                throw new IllegalArgumentException("产品名称长度不能超过100个字符");
            }
            product.setProductName(productName);
        }

        // 验证并更新描述
        if (StringUtil.isNotBlank(description)) {
            product.setDescription(description);
        }

        // 验证并更新价格
        if (price != null) {
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                LoggerUtil.warn("更新产品失败: 产品价格无效，产品ID: {}", productId);
                throw new IllegalArgumentException("产品价格必须大于0");
            }
            product.setPrice(price);
        }

        // 验证并更新适用天气
        if (StringUtil.isNotBlank(applicableWeather)) {
            product.setApplicableWeather(applicableWeather);
        }

        AgriculturalProduct updatedProduct = productRepository.save(product);
        LoggerUtil.info("产品信息更新成功，产品ID: {}", productId);

        return updatedProduct;
    }

    /**
     * 更新产品库存
     * 增加或减少产品的库存数量
     *
     * @param productId 产品ID
     * @param quantity 库存变化数量（正数表示增加，负数表示减少）
     * @return 更新后的产品对象
     * @throws IllegalArgumentException 当产品不存在或库存不足时抛出异常
     */
    public AgriculturalProduct updateProductStock(Long productId, Integer quantity) {
        LoggerUtil.info("开始更新产品库存，产品ID: {}, 库存变化: {}", productId, quantity);

        // 获取产品
        AgriculturalProduct product = getProductById(productId);

        // 验证库存变化数量
        if (quantity == null) {
            LoggerUtil.warn("更新库存失败: 库存变化数量为空，产品ID: {}", productId);
            throw new IllegalArgumentException("库存变化数量不能为空");
        }

        // 计算新库存
        int newStock = product.getStock() + quantity;

        // 验证新库存不能为负数
        if (newStock < 0) {
            LoggerUtil.warn("更新库存失败: 库存不足，产品ID: {}, 当前库存: {}, 减少数量: {}", 
                    productId, product.getStock(), Math.abs(quantity));
            throw new IllegalArgumentException("库存不足，无法完成此操作");
        }

        // 更新库存
        product.setStock(newStock);
        AgriculturalProduct updatedProduct = productRepository.save(product);

        LoggerUtil.info("产品库存更新成功，产品ID: {}, 新库存: {}", productId, newStock);

        return updatedProduct;
    }

    /**
     * 检查产品库存是否充足
     * 检查指定产品的库存是否大于等于指定数量
     *
     * @param productId 产品ID
     * @param requiredQuantity 所需数量
     * @return 如果库存充足返回true，否则返回false
     * @throws IllegalArgumentException 当产品不存在或参数无效时抛出异常
     */
    public boolean isStockSufficient(Long productId, Integer requiredQuantity) {
        LoggerUtil.info("检查产品库存是否充足，产品ID: {}, 所需数量: {}", productId, requiredQuantity);

        // 获取产品
        AgriculturalProduct product = getProductById(productId);

        // 验证所需数量
        if (requiredQuantity == null || requiredQuantity <= 0) {
            LoggerUtil.warn("检查库存失败: 所需数量无效，产品ID: {}", productId);
            throw new IllegalArgumentException("所需数量必须大于0");
        }

        boolean isSufficient = product.getStock() >= requiredQuantity;
        LoggerUtil.info("库存充足检查完成，产品ID: {}, 当前库存: {}, 所需数量: {}, 是否充足: {}", 
                productId, product.getStock(), requiredQuantity, isSufficient);

        return isSufficient;
    }

    /**
     * 扣减产品库存
     * 用于订单创建时扣减库存
     *
     * @param productId 产品ID
     * @param quantity 扣减数量
     * @return 更新后的产品对象
     * @throws IllegalArgumentException 当产品不存在或库存不足时抛出异常
     */
    public AgriculturalProduct deductStock(Long productId, Integer quantity) {
        LoggerUtil.info("开始扣减产品库存，产品ID: {}, 扣减数量: {}", productId, quantity);

        // 验证扣减数量
        if (quantity == null || quantity <= 0) {
            LoggerUtil.warn("扣减库存失败: 扣减数量无效，产品ID: {}", productId);
            throw new IllegalArgumentException("扣减数量必须大于0");
        }

        // 检查库存是否充足
        if (!isStockSufficient(productId, quantity)) {
            LoggerUtil.warn("扣减库存失败: 库存不足，产品ID: {}, 所需数量: {}", productId, quantity);
            throw new IllegalArgumentException("库存不足，无法完成此操作");
        }

        // 扣减库存
        return updateProductStock(productId, -quantity);
    }

    /**
     * 增加产品库存
     * 用于商家补货时增加库存
     *
     * @param productId 产品ID
     * @param quantity 增加数量
     * @return 更新后的产品对象
     * @throws IllegalArgumentException 当产品不存在或参数无效时抛出异常
     */
    public AgriculturalProduct increaseStock(Long productId, Integer quantity) {
        LoggerUtil.info("开始增加产品库存，产品ID: {}, 增加数量: {}", productId, quantity);

        // 验证增加数量
        if (quantity == null || quantity <= 0) {
            LoggerUtil.warn("增加库存失败: 增加数量无效，产品ID: {}", productId);
            throw new IllegalArgumentException("增加数量必须大于0");
        }

        // 增加库存
        return updateProductStock(productId, quantity);
    }

    /**
     * 删除产品
     * 从数据库中删除指定的产品记录
     *
     * @param productId 产品ID
     * @throws IllegalArgumentException 当产品不存在时抛出异常
     */
    public void deleteProduct(Long productId) {
        LoggerUtil.info("开始删除产品，产品ID: {}", productId);

        // 验证产品是否存在
        AgriculturalProduct product = getProductById(productId);

        productRepository.deleteById(productId);
        LoggerUtil.info("产品删除成功，产品ID: {}, 产品名称: {}", productId, product.getProductName());
    }

    /**
     * 获取库存不足的产品列表
     * 获取所有库存为0的产品
     *
     * @return 库存不足的产品列表
     */
    public List<AgriculturalProduct> getLowStockProducts() {
        LoggerUtil.info("获取库存不足的产品列表");

        List<AgriculturalProduct> allProducts = productRepository.findAll();
        List<AgriculturalProduct> lowStockProducts = allProducts.stream()
                .filter(p -> p.getStock() == 0)
                .toList();

        LoggerUtil.info("获取库存不足的产品列表成功，产品数: {}", lowStockProducts.size());

        return lowStockProducts;
    }

    /**
     * 获取指定商家的库存统计信息
     * 统计商家的产品总数、库存总数、库存不足产品数等信息
     *
     * @param merchantId 商家ID
     * @return 包含库存统计信息的字符串
     * @throws IllegalArgumentException 当商家ID无效时抛出异常
     */
    public String getStockStatistics(Long merchantId) {
        LoggerUtil.info("获取商家库存统计信息，商家ID: {}", merchantId);

        // 验证商家ID
        if (merchantId == null || merchantId <= 0) {
            LoggerUtil.warn("获取库存统计失败: 商家ID无效");
            throw new IllegalArgumentException("商家ID无效");
        }

        List<AgriculturalProduct> products = getProductsByMerchant(merchantId);

        int totalProducts = products.size();
        int totalStock = products.stream()
                .mapToInt(AgriculturalProduct::getStock)
                .sum();
        long lowStockCount = products.stream()
                .filter(p -> p.getStock() == 0)
                .count();

        String statistics = String.format(
                "商家ID: %d, 产品总数: %d, 库存总数: %d, 库存不足产品数: %d",
                merchantId, totalProducts, totalStock, lowStockCount
        );

        LoggerUtil.info("库存统计信息: {}", statistics);

        return statistics;
    }

    /**
     * 检查产品是否存在
     * 用于验证产品是否存在
     *
     * @param productId 产品ID
     * @return 如果产品存在返回true，否则返回false
     */
    public boolean productExists(Long productId) {
        LoggerUtil.info("检查产品是否存在，产品ID: {}", productId);

        if (productId == null || productId <= 0) {
            return false;
        }

        return productRepository.existsById(productId);
    }

    /**
     * 获取产品总数
     * 获取系统中所有产品的总数
     *
     * @return 产品总数
     */
    public long getProductCount() {
        LoggerUtil.info("获取产品总数");

        long count = productRepository.count();
        LoggerUtil.info("获取产品总数成功，产品总数: {}", count);

        return count;
    }

    /**
     * 获取指定商家的产品总数
     * 获取指定商家的产品数量
     *
     * @param merchantId 商家ID
     * @return 产品总数
     * @throws IllegalArgumentException 当商家ID无效时抛出异常
     */
    public long getProductCountByMerchant(Long merchantId) {
        LoggerUtil.info("获取商家产品总数，商家ID: {}", merchantId);

        // 验证商家ID
        if (merchantId == null || merchantId <= 0) {
            LoggerUtil.warn("获取产品总数失败: 商家ID无效");
            throw new IllegalArgumentException("商家ID无效");
        }

        long count = getProductsByMerchant(merchantId).size();
        LoggerUtil.info("获取商家产品总数成功，商家ID: {}, 产品总数: {}", merchantId, count);

        return count;
    }

    /**
     * 动态条件查询农资产品
     */
    public List<AgriculturalProduct> getProductsByCondition(String productName, String category,
                                                            BigDecimal minPrice, BigDecimal maxPrice) {
        // 1. 创建探测对象
        AgriculturalProduct probe = new AgriculturalProduct();

        if (StringUtil.isNotBlank(productName)) {
            probe.setProductName(productName);
        }
        if (StringUtil.isNotBlank(category)) {
            probe.setCategory(category);
        }

        // 2. 配置匹配器：忽略 null，并且对产品名称使用模糊包含匹配 (Contains)
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("productName", ExampleMatcher.GenericPropertyMatchers.contains());

        // 3. 执行 QBE 查询
        Example<AgriculturalProduct> example = Example.of(probe, matcher);
        List<AgriculturalProduct> products = productRepository.findAll(example);

        // 4. 价格范围过滤（如果前端传了价格区间）
        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        return products;
    }
}
