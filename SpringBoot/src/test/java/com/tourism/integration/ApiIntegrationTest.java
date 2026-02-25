package com.tourism.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourism.entity.*;
import com.tourism.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API集成测试 - 验证所有API端点的连接和前后端数据流
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttractionRepository attractionRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private BrowsingHistoryRepository browsingHistoryRepository;
    
    private User testUser;
    private User adminUser;
    private Attraction testAttraction;
    private Hotel testHotel;
    private Product testProduct;
    
    @BeforeEach
    public void setUp() {
        // 清空所有数据
        browsingHistoryRepository.deleteAll();
        favoriteRepository.deleteAll();
        commentRepository.deleteAll();
        orderRepository.deleteAll();
        routeRepository.deleteAll();
        productRepository.deleteAll();
        hotelRepository.deleteAll();
        attractionRepository.deleteAll();
        userRepository.deleteAll();
        announcementRepository.deleteAll();
        
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setRealName("Test User");
        testUser.setRole("tourist");
        testUser.setStatus("active");
        testUser = userRepository.save(testUser);
        
        // 创建管理员用户
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setPhone("13900139000");
        adminUser.setRealName("Admin User");
        adminUser.setRole("admin");
        adminUser.setStatus("active");
        adminUser = userRepository.save(adminUser);
        
        // 创建测试景点
        testAttraction = new Attraction();
        testAttraction.setName("广州塔");
        testAttraction.setDescription("广州的标志性建筑");
        testAttraction.setLocation("广州市天河区");
        testAttraction.setTicketPrice(new BigDecimal("150.00"));
        testAttraction.setOpeningHours("09:00-23:00");
        testAttraction.setImageUrl("http://example.com/tower.jpg");
        testAttraction.setIsGuangzhouSpecial(true);
        testAttraction = attractionRepository.save(testAttraction);
        
        // 创建测试酒店
        testHotel = new Hotel();
        testHotel.setName("五星酒店");
        testHotel.setLocation("广州市天河区");
        testHotel.setDescription("豪华五星级酒店");
        testHotel.setImageUrl("http://example.com/hotel.jpg");
        testHotel.setRating(new BigDecimal("5.0"));
        testHotel = hotelRepository.save(testHotel);
        
        // 创建测试商品
        testProduct = new Product();
        testProduct.setName("广州特色美食");
        testProduct.setDescription("正宗广州美食");
        testProduct.setPrice(new BigDecimal("99.00"));
        testProduct.setStock(100);
        testProduct.setImageUrl("http://example.com/food.jpg");
        testProduct.setCategory("美食");
        testProduct.setIsGuangzhouSpecial(true);
        testProduct = productRepository.save(testProduct);
    }
    
    // ==================== 用户管理API测试 ====================
    
    @Test
    public void testUserRegistration() throws Exception {
        String registerJson = objectMapper.writeValueAsString(new Object() {
            public String username = "newuser";
            public String password = "password123";
            public String email = "newuser@example.com";
            public String phone = "13700137000";
        });
        
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.role").value("tourist"));
    }
    
    @Test
    public void testUserLogin() throws Exception {
        String loginJson = objectMapper.writeValueAsString(new Object() {
            public String username = "testuser";
            public String password = "password123";
        });
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.id").value(testUser.getId().intValue()));
    }
    
    @Test
    public void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/api/users/" + testUser.getId())
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }
    
    @Test
    public void testUpdateUserProfile() throws Exception {
        String updateJson = objectMapper.writeValueAsString(new Object() {
            public String realName = "Updated Name";
            public String email = "updated@example.com";
            public String phone = "13600136000";
        });
        
        mockMvc.perform(put("/api/users/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist")
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.realName").value("Updated Name"));
    }
    
    // ==================== 景点管理API测试 ====================
    
    @Test
    public void testGetAttractionList() throws Exception {
        mockMvc.perform(get("/api/attractions/list")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.attractions", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(1)));
    }
    
    @Test
    public void testGetAttractionDetail() throws Exception {
        mockMvc.perform(get("/api/attractions/" + testAttraction.getId())
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("广州塔"))
                .andExpect(jsonPath("$.data.isGuangzhouSpecial").value(true));
    }
    
    @Test
    public void testSearchAttractions() throws Exception {
        mockMvc.perform(get("/api/attractions/search")
                .param("keyword", "广州")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }
    
    @Test
    public void testGetGuangzhouSpecialAttractions() throws Exception {
        mockMvc.perform(get("/api/attractions/guangzhou-special")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.attractions", hasSize(greaterThanOrEqualTo(1))));
    }
    
    @Test
    public void testCreateAttraction() throws Exception {
        String createJson = objectMapper.writeValueAsString(new Object() {
            public String name = "新景点";
            public String description = "新景点描述";
            public String location = "广州市";
            public BigDecimal ticketPrice = new BigDecimal("100.00");
            public String openingHours = "09:00-18:00";
            public String imageUrl = "http://example.com/new.jpg";
            public Boolean isGuangzhouSpecial = true;
        });
        
        mockMvc.perform(post("/api/attractions/admin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", adminUser.getId())
                .header("X-User-Role", "admin")
                .content(createJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("新景点"));
    }
    
    // ==================== 酒店管理API测试 ====================
    
    @Test
    public void testGetHotelList() throws Exception {
        mockMvc.perform(get("/api/hotels/list")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.hotels", hasSize(greaterThanOrEqualTo(1))));
    }
    
    @Test
    public void testGetHotelDetail() throws Exception {
        mockMvc.perform(get("/api/hotels/" + testHotel.getId())
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("五星酒店"));
    }
    
    // ==================== 商品管理API测试 ====================
    
    @Test
    public void testGetProductList() throws Exception {
        mockMvc.perform(get("/api/products/list")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.products", hasSize(greaterThanOrEqualTo(1))));
    }
    
    @Test
    public void testGetProductDetail() throws Exception {
        mockMvc.perform(get("/api/products/" + testProduct.getId())
                .param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("广州特色美食"));
    }
    
    // ==================== 订单管理API测试 ====================
    
    @Test
    public void testCreateAttractionOrder() throws Exception {
        String orderJson = objectMapper.writeValueAsString(new Object() {
            public Long userId = testUser.getId();
            public Long attractionId = testAttraction.getId();
            public Integer quantity = 2;
            public String visitDate = "2024-03-01";
        });
        
        mockMvc.perform(post("/api/orders/attractions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist")
                .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orderType").value("attraction"))
                .andExpect(jsonPath("$.data.status").value("pending"));
    }
    
    @Test
    public void testGetUserOrders() throws Exception {
        mockMvc.perform(get("/api/orders/user/" + testUser.getId())
                .param("page", "0")
                .param("size", "10")
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.orders").isArray());
    }
    
    // ==================== 留言评价API测试 ====================
    
    @Test
    public void testCreateComment() throws Exception {
        String commentJson = objectMapper.writeValueAsString(new Object() {
            public Long userId = testUser.getId();
            public String targetType = "attraction";
            public Long targetId = testAttraction.getId();
            public String content = "很好的景点";
            public Integer rating = 5;
        });
        
        mockMvc.perform(post("/api/comments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist")
                .content(commentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.rating").value(5));
    }
    
    @Test
    public void testGetCommentsByTarget() throws Exception {
        mockMvc.perform(get("/api/comments/target/attraction/" + testAttraction.getId())
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.comments").isArray());
    }
    
    // ==================== 公告管理API测试 ====================
    
    @Test
    public void testCreateAnnouncement() throws Exception {
        String announcementJson = objectMapper.writeValueAsString(new Object() {
            public String title = "系统公告";
            public String content = "这是一条系统公告";
        });
        
        mockMvc.perform(post("/api/announcements/admin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", adminUser.getId())
                .header("X-User-Role", "admin")
                .content(announcementJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("系统公告"));
    }
    
    @Test
    public void testGetAnnouncements() throws Exception {
        mockMvc.perform(get("/api/announcements/list")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.announcements").isArray());
    }
    
    // ==================== 收藏功能API测试 ====================
    
    @Test
    public void testAddFavorite() throws Exception {
        String favoriteJson = objectMapper.writeValueAsString(new Object() {
            public Long userId = testUser.getId();
            public String targetType = "attraction";
            public Long targetId = testAttraction.getId();
        });
        
        mockMvc.perform(post("/api/favorites/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist")
                .content(favoriteJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }
    
    @Test
    public void testGetUserFavorites() throws Exception {
        mockMvc.perform(get("/api/favorites/user/" + testUser.getId())
                .param("page", "0")
                .param("size", "10")
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.favorites").isArray());
    }
    
    // ==================== 浏览历史API测试 ====================
    
    @Test
    public void testGetBrowsingHistory() throws Exception {
        mockMvc.perform(get("/api/browsing-history/user/" + testUser.getId())
                .param("page", "0")
                .param("size", "10")
                .header("X-User-Id", testUser.getId())
                .header("X-User-Role", "tourist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.history").isArray());
    }
    
    // ==================== 路线推荐API测试 ====================
    
    @Test
    public void testGetRoutes() throws Exception {
        mockMvc.perform(get("/api/routes/list")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.routes").isArray());
    }
    
    // ==================== 前后端数据流测试 ====================
    
    @Test
    public void testCompleteUserJourney() throws Exception {
        // 1. 用户注册
        String registerJson = objectMapper.writeValueAsString(new Object() {
            public String username = "journeyuser";
            public String password = "password123";
            public String email = "journey@example.com";
            public String phone = "13500135000";
        });
        
        MvcResult registerResult = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isOk())
                .andReturn();
        
        String registerResponse = registerResult.getResponse().getContentAsString();
        Long userId = objectMapper.readTree(registerResponse).get("data").get("id").asLong();
        
        // 2. 用户登录
        String loginJson = objectMapper.writeValueAsString(new Object() {
            public String username = "journeyuser";
            public String password = "password123";
        });
        
        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId.intValue()));
        
        // 3. 浏览景点
        mockMvc.perform(get("/api/attractions/" + testAttraction.getId())
                .param("userId", userId.toString()))
                .andExpect(status().isOk());
        
        // 4. 创建订单
        String orderJson = objectMapper.writeValueAsString(new Object() {
            public Long userId_val = userId;
            public Long attractionId = testAttraction.getId();
            public Integer quantity = 1;
            public String visitDate = "2024-03-01";
        });
        
        mockMvc.perform(post("/api/orders/attractions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", userId)
                .header("X-User-Role", "tourist")
                .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
        
        // 5. 查看订单
        mockMvc.perform(get("/api/orders/user/" + userId)
                .param("page", "0")
                .param("size", "10")
                .header("X-User-Id", userId)
                .header("X-User-Role", "tourist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orders", hasSize(greaterThanOrEqualTo(1))));
    }
    
    @Test
    public void testAdminManagementJourney() throws Exception {
        // 1. 管理员创建景点
        String createAttractionJson = objectMapper.writeValueAsString(new Object() {
            public String name = "管理员创建景点";
            public String description = "测试景点";
            public String location = "广州";
            public BigDecimal ticketPrice = new BigDecimal("80.00");
            public String openingHours = "09:00-18:00";
            public String imageUrl = "http://example.com/test.jpg";
            public Boolean isGuangzhouSpecial = false;
        });
        
        mockMvc.perform(post("/api/attractions/admin/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", adminUser.getId())
                .header("X-User-Role", "admin")
                .content(createAttractionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
        
        // 2. 管理员查看所有用户
        mockMvc.perform(get("/api/users/admin/list")
                .param("page", "0")
                .param("size", "10")
                .header("X-User-Id", adminUser.getId())
                .header("X-User-Role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users", hasSize(greaterThanOrEqualTo(2))));
        
        // 3. 管理员查看所有订单
        mockMvc.perform(get("/api/orders/admin/all")
                .param("page", "0")
                .param("size", "10")
                .header("X-User-Id", adminUser.getId())
                .header("X-User-Role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"));
    }
}
