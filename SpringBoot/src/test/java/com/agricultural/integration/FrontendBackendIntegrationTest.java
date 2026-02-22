package com.agricultural.integration;

import com.agricultural.dto.LoginResponse;
import com.agricultural.dto.Result;
import com.agricultural.entity.*;
import com.agricultural.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 前后端集成测试
 * 
 * 验证前后端接口的数据一致性和交互正确性
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FrontendBackendIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WarningService warningService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    private String authToken;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        // 创建测试用户
        testUser = userService.register(
                "test_user",
                "password123",
                "test@example.com",
                "13800138000",
                User.UserType.FARMER,
                "河南省郑州市"
        );

        // 模拟登录获取token
        MvcResult loginResult = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "test_user")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        Result<LoginResponse> result = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructParametricType(Result.class, LoginResponse.class));
        
        authToken = result.getData().getToken();
    }

    /**
     * 测试用户登录响应格式
     * 验证登录响应包含所有必要的用户信息
     */
    @Test
    void testLoginResponseFormat() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "test_user")
                .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.userType").value("FARMER"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Result<LoginResponse> response = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructParametricType(Result.class, LoginResponse.class));

        assertNotNull(response.getData());
        assertNotNull(response.getData().getToken());
        assertEquals("test_user", response.getData().getUsername());
    }

    /**
     * 测试用户注册响应格式
     * 验证注册响应包含正确的数据结构
     */
    @Test
    void testRegisterResponseFormat() throws Exception {
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "new_user")
                .param("password", "password123")
                .param("email", "new@example.com")
                .param("userType", "FARMER")
                .param("region", "河南省开封市"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.username").value("new_user"))
                .andReturn();
    }

    /**
     * 测试获取用户信息响应格式
     * 验证用户信息响应的数据一致性
     */
    @Test
    void testGetUserInfoResponseFormat() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(testUser.getId()))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andReturn();
    }

    /**
     * 测试气象数据响应格式
     * 验证气象数据API返回正确的数据结构
     */
    @Test
    void testWeatherDataResponseFormat() throws Exception {
        // 创建测试气象数据
        weatherService.saveWeatherData(
                "河南省郑州市",
                new BigDecimal("25.5"),
                65,
                new BigDecimal("0.0"),
                new BigDecimal("3.2"),
                "晴天"
        );

        mockMvc.perform(get("/api/weather/current")
                .param("region", "河南省郑州市")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.region").value("河南省郑州市"))
                .andExpect(jsonPath("$.data.temperature").exists())
                .andExpect(jsonPath("$.data.humidity").exists())
                .andReturn();
    }

    /**
     * 测试预警列表响应格式
     * 验证预警列表API返回正确的数据结构
     */
    @Test
    void testWarningListResponseFormat() throws Exception {
        // 创建测试预警
        warningService.publishWarning(
                "暴雨",
                "河南省郑州市",
                Warning.WarningSeverity.HIGH,
                "预计今晚有暴雨",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(4)
        );

        mockMvc.perform(get("/api/warnings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();
    }

    /**
     * 测试产品列表响应格式
     * 验证产品列表API返回正确的数据结构
     */
    @Test
    void testProductListResponseFormat() throws Exception {
        // 创建测试商家和产品
        User merchant = userService.register(
                "test_merchant",
                "password123",
                "merchant@example.com",
                "13900139000",
                User.UserType.MERCHANT,
                "河南省郑州市"
        );

        productService.addProduct(
                "防雨农膜",
                "防护用品",
                "高质量防雨农膜",
                new BigDecimal("89.99"),
                100,
                merchant.getId(),
                "暴雨"
        );

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();
    }

    /**
     * 测试订单创建响应格式
     * 验证订单创建API返回正确的数据结构
     */
    @Test
    void testOrderCreationResponseFormat() throws Exception {
        // 创建测试商家和产品
        User merchant = userService.register(
                "order_merchant",
                "password123",
                "order_merchant@example.com",
                "13900139001",
                User.UserType.MERCHANT,
                "河南省郑州市"
        );

        AgriculturalProduct product = productService.addProduct(
                "复合肥",
                "肥料",
                "高效复合肥",
                new BigDecimal("45.50"),
                200,
                merchant.getId(),
                "通用"
        );

        // 创建订单
        Order order = orderService.createOrder(
                testUser.getId(),
                product.getId(),
                10,
                "河南省郑州市中原区"
        );

        mockMvc.perform(get("/api/orders/" + order.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(order.getId()))
                .andExpect(jsonPath("$.data.userId").value(testUser.getId()))
                .andExpect(jsonPath("$.data.quantity").value(10))
                .andReturn();
    }

    /**
     * 测试错误响应格式
     * 验证错误响应包含正确的错误信息
     */
    @Test
    void testErrorResponseFormat() throws Exception {
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "nonexistent_user")
                .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andReturn();
    }

    /**
     * 测试数据一致性
     * 验证创建的数据与返回的数据一致
     */
    @Test
    void testDataConsistency() throws Exception {
        // 创建产品
        User merchant = userService.register(
                "consistency_merchant",
                "password123",
                "consistency@example.com",
                "13900139002",
                User.UserType.MERCHANT,
                "河南省郑州市"
        );

        AgriculturalProduct product = productService.addProduct(
                "测试产品",
                "测试类别",
                "测试描述",
                new BigDecimal("99.99"),
                50,
                merchant.getId(),
                "测试天气"
        );

        // 获取产品信息
        MvcResult result = mockMvc.perform(get("/api/products/" + product.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Result<AgriculturalProduct> response = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructParametricType(Result.class, AgriculturalProduct.class));

        AgriculturalProduct returnedProduct = response.getData();
        assertEquals(product.getId(), returnedProduct.getId());
        assertEquals(product.getProductName(), returnedProduct.getProductName());
        assertEquals(product.getCategory(), returnedProduct.getCategory());
        assertEquals(product.getPrice(), returnedProduct.getPrice());
        assertEquals(product.getStock(), returnedProduct.getStock());
    }

    /**
     * 测试授权验证
     * 验证未授权请求被正确拒绝
     */
    @Test
    void testAuthorizationValidation() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    /**
     * 测试CORS配置
     * 验证跨域请求被正确处理
     */
    @Test
    void testCORSConfiguration() throws Exception {
        mockMvc.perform(options("/api/users/login")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andReturn();
    }

    /**
     * 测试响应时间戳
     * 验证所有响应都包含时间戳
     */
    @Test
    void testResponseTimestamp() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/" + testUser.getId())
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(responseBody, Result.class);
        assertNotNull(response.getTimestamp());
    }

    /**
     * 测试批量数据响应
     * 验证列表API返回正确的数据结构
     */
    @Test
    void testBatchDataResponse() throws Exception {
        // 创建多个预警
        for (int i = 0; i < 3; i++) {
            warningService.publishWarning(
                    "测试预警" + i,
                    "河南省郑州市",
                    Warning.WarningSeverity.MEDIUM,
                    "测试描述" + i,
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusHours(4)
            );
        }

        MvcResult result = mockMvc.perform(get("/api/warnings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        Result<?> response = objectMapper.readValue(responseBody, Result.class);
        assertNotNull(response.getData());
    }
}
