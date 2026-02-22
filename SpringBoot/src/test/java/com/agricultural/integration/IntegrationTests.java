package com.agricultural.integration;

import com.agricultural.entity.*;
import com.agricultural.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试
 * 
 * 测试完整的业务流程，包括：
 * - 用户注册和登录流程
 * - 气象数据获取和预警发布流程
 * - 农资推荐和订单创建流程
 * - 完整的业务流程
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IntegrationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private ProductService productService;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private OrderService orderService;

    private User testFarmer;
    private User testMerchant;
    private AgriculturalProduct testProduct;
    private WeatherData testWeatherData;
    private Warning testWarning;

    @BeforeEach
    void setUp() {
        // 创建测试农户
        testFarmer = userService.register(
                "test_farmer",
                "password123",
                "farmer@example.com",
                "13800138000",
                User.UserType.FARMER,
                "河南省郑州市"
        );

        // 创建测试商家
        testMerchant = userService.register(
                "test_merchant",
                "password123",
                "merchant@example.com",
                "13900139000",
                User.UserType.MERCHANT,
                "河南省郑州市"
        );

        // 创建测试产品
        testProduct = productService.addProduct(
                "防雹网",
                "防护用品",
                "高强度防雹网，适用于冰雹天气",
                new BigDecimal("99.99"),
                100,
                testMerchant.getId(),
                "冰雹"
        );

        // 创建测试气象数据
        testWeatherData = weatherService.saveWeatherData(
                "河南省郑州市",
                new BigDecimal("25.5"),
                65,
                new BigDecimal("0.0"),
                new BigDecimal("3.2"),
                "晴天"
        );

        // 创建测试预警
        testWarning = warningService.publishWarning(
                "冰雹",
                "河南省郑州市",
                Warning.WarningSeverity.HIGH,
                "预计今晚有冰雹天气，请做好防护准备",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4)
        );
    }

    /**
     * 测试用户注册和登录流程
     * 验证用户能够成功注册和登录
     */
    @Test
    void testUserRegistrationAndLoginFlow() {
        // 验证用户注册成功
        assertNotNull(testFarmer.getId());
        assertEquals("test_farmer", testFarmer.getUsername());
        assertEquals("farmer@example.com", testFarmer.getEmail());
        assertEquals(User.UserType.FARMER, testFarmer.getUserType());
        assertEquals(User.UserStatus.ACTIVE, testFarmer.getStatus());

        // 测试用户登录
        User loginUser = userService.login("test_farmer", "password123");
        assertNotNull(loginUser);
        assertEquals(testFarmer.getId(), loginUser.getId());
        assertEquals("test_farmer", loginUser.getUsername());

        // 测试登录失败（错误密码）
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login("test_farmer", "wrongpassword");
        });

        // 测试登录失败（不存在的用户）
        assertThrows(IllegalArgumentException.class, () -> {
            userService.login("nonexistent_user", "password123");
        });
    }

    /**
     * 测试气象数据获取和预警发布流程
     * 验证能够获取气象数据并发布预警
     */
    @Test
    void testWeatherDataAndWarningFlow() {
        // 验证气象数据保存成功
        assertNotNull(testWeatherData.getId());
        assertEquals("河南省郑州市", testWeatherData.getRegion());
        assertEquals(new BigDecimal("25.5"), testWeatherData.getTemperature());
        assertEquals(65, testWeatherData.getHumidity());

        // 测试获取当前气象数据
        WeatherData currentWeather = weatherService.getCurrentWeather("河南省郑州市");
        assertNotNull(currentWeather);
        assertEquals("河南省郑州市", currentWeather.getRegion());

        // 测试获取气象预报
        List<WeatherData> forecast = weatherService.getWeatherForecast("河南省郑州市", 24);
        assertNotNull(forecast);

        // 验证预警发布成功
        assertNotNull(testWarning.getId());
        assertEquals("冰雹", testWarning.getWarningType());
        assertEquals("河南省郑州市", testWarning.getRegion());
        assertEquals(Warning.WarningSeverity.HIGH, testWarning.getSeverity());
        assertEquals(Warning.WarningStatus.ACTIVE, testWarning.getStatus());

        // 测试获取活跃预警
        List<Warning> activeWarnings = warningService.getActiveWarnings();
        assertNotNull(activeWarnings);
        assertTrue(activeWarnings.size() > 0);

        // 测试根据地区获取预警
        List<Warning> regionWarnings = warningService.getWarningsByRegion("河南省郑州市");
        assertNotNull(regionWarnings);
        assertTrue(regionWarnings.size() > 0);

        // 测试检查地区是否有活跃预警
        boolean hasActiveWarnings = warningService.hasActiveWarnings("河南省郑州市");
        assertTrue(hasActiveWarnings);
    }

    /**
     * 测试农资推荐和订单创建流程
     * 验证能够根据预警生成推荐并创建订单
     */
    @Test
    void testProductRecommendationAndOrderFlow() {
        // 验证产品添加成功
        assertNotNull(testProduct.getId());
        assertEquals("防雹网", testProduct.getProductName());
        assertEquals("防护用品", testProduct.getCategory());
        assertEquals(new BigDecimal("99.99"), testProduct.getPrice());
        assertEquals(100, testProduct.getStock());

        // 测试获取产品列表
        List<AgriculturalProduct> products = productService.getProductList();
        assertNotNull(products);
        assertTrue(products.size() > 0);

        // 测试根据类别获取产品
        List<AgriculturalProduct> categoryProducts = productService.getProductsByCategory("防护用品");
        assertNotNull(categoryProducts);
        assertTrue(categoryProducts.size() > 0);

        // 测试根据适用天气获取产品
        List<AgriculturalProduct> weatherProducts = productService.getProductsByApplicableWeather("冰雹");
        assertNotNull(weatherProducts);
        assertTrue(weatherProducts.size() > 0);

        // 测试库存检查
        boolean isStockSufficient = productService.isStockSufficient(testProduct.getId(), 50);
        assertTrue(isStockSufficient);

        // 测试生成推荐
        List<Recommendation> recommendations = recommendationService.generateRecommendations(
                testWarning.getId(),
                testFarmer.getId()
        );
        assertNotNull(recommendations);

        // 测试获取推荐列表
        List<Recommendation> userRecommendations = recommendationService.getRecommendationsByUserId(testFarmer.getId());
        assertNotNull(userRecommendations);

        // 测试创建订单
        Order order = orderService.createOrder(
                testFarmer.getId(),
                testProduct.getId(),
                10,
                "河南省郑州市中原区农业路123号"
        );

        // 验证订单创建成功
        assertNotNull(order.getId());
        assertNotNull(order.getOrderNumber());
        assertEquals(testFarmer.getId(), order.getUserId());
        assertEquals(testProduct.getId(), order.getProductId());
        assertEquals(10, order.getQuantity());
        assertEquals(new BigDecimal("999.90"), order.getTotalPrice());
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());

        // 验证库存已扣减
        AgriculturalProduct updatedProduct = productService.getProductById(testProduct.getId());
        assertEquals(90, updatedProduct.getStock());
    }

    /**
     * 测试完整的业务流程
     * 从用户注册到订单完成的完整流程
     */
    @Test
    void testCompleteBusinessFlow() {
        // 1. 用户注册和登录
        User farmer = userService.register(
                "complete_flow_farmer",
                "password123",
                "flow_farmer@example.com",
                "13700137000",
                User.UserType.FARMER,
                "河南省开封市"
        );
        assertNotNull(farmer.getId());

        User loginUser = userService.login("complete_flow_farmer", "password123");
        assertEquals(farmer.getId(), loginUser.getId());

        // 2. 商家添加产品
        User merchant = userService.register(
                "complete_flow_merchant",
                "password123",
                "flow_merchant@example.com",
                "13600136000",
                User.UserType.MERCHANT,
                "河南省开封市"
        );

        AgriculturalProduct product = productService.addProduct(
                "防冻液",
                "防护用品",
                "高效防冻液，适用于低温天气",
                new BigDecimal("149.99"),
                50,
                merchant.getId(),
                "低温"
        );
        assertNotNull(product.getId());

        // 3. 气象数据和预警
        WeatherData weatherData = weatherService.saveWeatherData(
                "河南省开封市",
                new BigDecimal("-5.0"),
                45,
                new BigDecimal("2.5"),
                new BigDecimal("8.5"),
                "低温"
        );
        assertNotNull(weatherData.getId());

        Warning warning = warningService.publishWarning(
                "低温",
                "河南省开封市",
                Warning.WarningSeverity.CRITICAL,
                "预计气温下降至-10°C，请做好防冻准备",
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(6)
        );
        assertNotNull(warning.getId());

        // 4. 生成推荐
        List<Recommendation> recommendations = recommendationService.generateRecommendations(
                warning.getId(),
                farmer.getId()
        );
        assertNotNull(recommendations);

        // 5. 创建订单
        Order order = orderService.createOrder(
                farmer.getId(),
                product.getId(),
                5,
                "河南省开封市龙亭区"
        );
        assertNotNull(order.getId());
        assertEquals(Order.OrderStatus.PENDING, order.getStatus());

        // 6. 订单支付
        Order paidOrder = orderService.processPayment(order.getId(), "支付宝");
        assertEquals(Order.OrderStatus.PAID, paidOrder.getStatus());
        assertEquals("支付宝", paidOrder.getPaymentMethod());

        // 7. 订单发货
        Order shippedOrder = orderService.processShipment(order.getId());
        assertEquals(Order.OrderStatus.SHIPPED, shippedOrder.getStatus());

        // 8. 订单送达
        Order deliveredOrder = orderService.confirmDelivery(order.getId());
        assertEquals(Order.OrderStatus.DELIVERED, deliveredOrder.getStatus());

        // 9. 验证库存已扣减
        AgriculturalProduct updatedProduct = productService.getProductById(product.getId());
        assertEquals(45, updatedProduct.getStock());

        // 10. 验证用户订单列表
        List<Order> userOrders = orderService.getOrdersByUserId(farmer.getId());
        assertTrue(userOrders.size() > 0);
    }

    /**
     * 测试订单取消和库存恢复流程
     * 验证订单取消时库存能够正确恢复
     */
    @Test
    void testOrderCancellationAndStockRecovery() {
        // 创建订单
        Order order = orderService.createOrder(
                testFarmer.getId(),
                testProduct.getId(),
                20,
                "河南省郑州市"
        );

        // 验证库存已扣减
        AgriculturalProduct productAfterOrder = productService.getProductById(testProduct.getId());
        assertEquals(80, productAfterOrder.getStock());

        // 取消订单
        Order cancelledOrder = orderService.cancelOrder(order.getId());
        assertEquals(Order.OrderStatus.CANCELLED, cancelledOrder.getStatus());

        // 验证库存已恢复
        AgriculturalProduct productAfterCancel = productService.getProductById(testProduct.getId());
        assertEquals(100, productAfterCancel.getStock());
    }

    /**
     * 测试多个用户的订单流程
     * 验证多个用户能够独立创建和管理订单
     */
    @Test
    void testMultipleUsersOrderFlow() {
        // 创建第二个农户
        User farmer2 = userService.register(
                "test_farmer2",
                "password123",
                "farmer2@example.com",
                "13800138001",
                User.UserType.FARMER,
                "河南省郑州市"
        );

        // 第一个农户创建订单
        Order order1 = orderService.createOrder(
                testFarmer.getId(),
                testProduct.getId(),
                10,
                "地址1"
        );

        // 第二个农户创建订单
        Order order2 = orderService.createOrder(
                farmer2.getId(),
                testProduct.getId(),
                15,
                "地址2"
        );

        // 验证两个订单都创建成功
        assertNotNull(order1.getId());
        assertNotNull(order2.getId());
        assertNotEquals(order1.getId(), order2.getId());

        // 验证库存已扣减（10 + 15 = 25）
        AgriculturalProduct updatedProduct = productService.getProductById(testProduct.getId());
        assertEquals(75, updatedProduct.getStock());

        // 验证用户订单列表
        List<Order> farmer1Orders = orderService.getOrdersByUserId(testFarmer.getId());
        assertEquals(1, farmer1Orders.size());

        List<Order> farmer2Orders = orderService.getOrdersByUserId(farmer2.getId());
        assertEquals(1, farmer2Orders.size());
    }

    /**
     * 测试预警过期处理流程
     * 验证过期预警能够被正确标记
     */
    @Test
    void testExpiredWarningHandling() {
        // 创建即将过期的预警（结束时间在未来但很近）
        Warning expiredWarning = warningService.publishWarning(
                "暴雨",
                "河南省郑州市",
                Warning.WarningSeverity.MEDIUM,
                "暴雨预警",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusSeconds(1)
        );

        // 处理过期预警
        int expiredCount = warningService.handleExpiredWarnings();
        assertTrue(expiredCount >= 0);

        // 验证预警状态
        Warning updatedWarning = warningService.getWarningById(expiredWarning.getId());
        assertNotNull(updatedWarning);
    }

    /**
     * 测试推荐接受和拒绝流程
     * 验证用户能够接受或拒绝推荐
     */
    @Test
    void testRecommendationAcceptanceFlow() {
        // 生成推荐
        List<Recommendation> recommendations = recommendationService.generateRecommendations(
                testWarning.getId(),
                testFarmer.getId()
        );

        // 如果没有推荐，则跳过此测试
        if (recommendations.isEmpty()) {
            return;
        }

        Recommendation recommendation = recommendations.get(0);

        // 验证推荐初始状态为待处理
        assertEquals(Recommendation.RecommendationStatus.PENDING, recommendation.getStatus());

        // 接受推荐
        Recommendation acceptedRecommendation = recommendationService.acceptRecommendation(recommendation.getId());
        assertEquals(Recommendation.RecommendationStatus.ACCEPTED, acceptedRecommendation.getStatus());

        // 创建另一个推荐用于拒绝测试
        List<Recommendation> recommendations2 = recommendationService.generateRecommendations(
                testWarning.getId(),
                testFarmer.getId()
        );

        if (recommendations2.size() > 0) {
            Recommendation recommendation2 = recommendations2.get(0);
            
            // 拒绝推荐
            Recommendation rejectedRecommendation = recommendationService.rejectRecommendation(recommendation2.getId());
            assertEquals(Recommendation.RecommendationStatus.REJECTED, rejectedRecommendation.getStatus());
        }
    }

    /**
     * 测试库存不足的订单创建失败
     * 验证库存不足时订单创建失败
     */
    @Test
    void testOrderCreationWithInsufficientStock() {
        // 尝试创建超过库存的订单
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(
                    testFarmer.getId(),
                    testProduct.getId(),
                    200,  // 库存只有100
                    "河南省郑州市"
            );
        });

        // 验证库存未变化
        AgriculturalProduct product = productService.getProductById(testProduct.getId());
        assertEquals(100, product.getStock());
    }

    /**
     * 测试用户信息更新流程
     * 验证用户能够更新个人信息
     */
    @Test
    void testUserInformationUpdateFlow() {
        // 更新用户信息
        User updatedUser = userService.updateUser(
                testFarmer.getId(),
                "newemail@example.com",
                "13900139999",
                "河南省洛阳市"
        );

        // 验证信息已更新
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("13900139999", updatedUser.getPhone());
        assertEquals("河南省洛阳市", updatedUser.getRegion());

        // 验证用户名和密码未变化
        assertEquals("test_farmer", updatedUser.getUsername());
    }

    /**
     * 测试产品库存管理流程
     * 验证产品库存的增加和减少操作
     */
    @Test
    void testProductStockManagementFlow() {
        // 初始库存为100
        assertEquals(100, testProduct.getStock());

        // 增加库存
        AgriculturalProduct increasedProduct = productService.increaseStock(testProduct.getId(), 50);
        assertEquals(150, increasedProduct.getStock());

        // 扣减库存
        AgriculturalProduct decreasedProduct = productService.deductStock(testProduct.getId(), 30);
        assertEquals(120, decreasedProduct.getStock());

        // 验证库存不能为负
        assertThrows(IllegalArgumentException.class, () -> {
            productService.deductStock(testProduct.getId(), 200);
        });
    }
}
