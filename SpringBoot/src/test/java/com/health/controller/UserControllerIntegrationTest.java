package com.health.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.health.dto.UserUpdateRequest;
import com.health.entity.User;
import com.health.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器集成测试
 * 测试用户个人信息管理和权限控制
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User adminUser;
    private Long userId;
    private Long adminId;

    @BeforeEach
    public void setUp() {
        // 清空数据库
        userRepository.deleteAll();

        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setName("测试用户");
        testUser.setAge(25);
        testUser.setGender("男");
        testUser.setPhone("13800138000");
        testUser.setRole("USER");
        testUser.setStatus("ACTIVE");
        testUser = userRepository.save(testUser);
        userId = testUser.getId();

        // 创建管理员用户
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setName("管理员");
        adminUser.setRole("ADMIN");
        adminUser.setStatus("ACTIVE");
        adminUser = userRepository.save(adminUser);
        adminId = adminUser.getId();
    }

    @Test
    public void testGetUserProfileSuccess() throws Exception {
        // 执行获取个人信息请求
        mockMvc.perform(get("/api/users/profile")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取个人信息成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.name").value("测试用户"));
    }

    @Test
    public void testGetUserProfileNonexistentUser() throws Exception {
        // 执行获取不存在用户的个人信息请求
        mockMvc.perform(get("/api/users/profile")
                .param("userId", "99999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("用户不存在")));
    }

    @Test
    public void testUpdateUserProfileSuccess() throws Exception {
        // 准备更新请求
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setName("新名字");
        updateRequest.setAge(30);
        updateRequest.setGender("女");
        updateRequest.setPhone("13900139000");

        // 执行更新个人信息请求
        mockMvc.perform(put("/api/users/profile")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新个人信息成功"))
                .andExpect(jsonPath("$.data.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.data.name").value("新名字"))
                .andExpect(jsonPath("$.data.age").value(30));

        // 验证数据库中的数据已更新
        User updatedUser = userRepository.findById(userId).orElse(null);
        assert updatedUser != null;
        assert updatedUser.getEmail().equals("newemail@example.com");
        assert updatedUser.getName().equals("新名字");
    }

    @Test
    public void testUpdateUserProfileInvalidEmail() throws Exception {
        // 准备更新请求（无效的邮箱）
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("invalid-email");

        // 执行更新个人信息请求
        mockMvc.perform(put("/api/users/profile")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("邮箱格式不正确")));
    }

    @Test
    public void testGetUserListAsAdminSuccess() throws Exception {
        // 创建多个用户
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword("password");
            user.setEmail("user" + i + "@example.com");
            user.setRole("USER");
            user.setStatus("ACTIVE");
            userRepository.save(user);
        }

        // 执行获取用户列表请求（管理员）
        mockMvc.perform(get("/api/users")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取用户列表成功"))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    public void testGetUserListAsNonAdminForbidden() throws Exception {
        // 执行获取用户列表请求（非管理员）
        mockMvc.perform(get("/api/users")
                .param("role", "USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("权限不足，只有管理员可以访问用户列表"));
    }

    @Test
    public void testGetUserListWithoutRoleForbidden() throws Exception {
        // 执行获取用户列表请求（不提供role参数）
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("权限不足，只有管理员可以访问用户列表"));
    }
}
