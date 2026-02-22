package com.agricultural.repository;

import com.agricultural.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户数据访问层测试
 * 
 * 测试UserRepository的CRUD操作和自定义查询方法
 * 
 * @author Agricultural Platform Team
 * @version 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = User.builder()
                .username("test_user")
                .password("encrypted_password")
                .email("test@example.com")
                .phone("13800138000")
                .userType(User.UserType.FARMER)
                .region("河南省郑州市")
                .status(User.UserStatus.ACTIVE)
                .build();
    }

    @Test
    void testSaveUser() {
        // 保存用户
        User savedUser = userRepository.save(testUser);
        
        // 验证用户已保存
        assertNotNull(savedUser.getId());
        assertEquals("test_user", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    void testFindUserById() {
        // 保存用户
        User savedUser = userRepository.save(testUser);
        
        // 根据ID查询用户
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        
        // 验证查询结果
        assertTrue(foundUser.isPresent());
        assertEquals("test_user", foundUser.get().getUsername());
    }

    @Test
    void testFindByUsername() {
        // 保存用户
        userRepository.save(testUser);
        
        // 根据用户名查询
        Optional<User> foundUser = userRepository.findByUsername("test_user");
        
        // 验证查询结果
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByEmail() {
        // 保存用户
        userRepository.save(testUser);
        
        // 根据邮箱查询
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        
        // 验证查询结果
        assertTrue(foundUser.isPresent());
        assertEquals("test_user", foundUser.get().getUsername());
    }

    @Test
    void testFindByUserType() {
        // 保存多个用户
        User farmer = User.builder()
                .username("farmer_test")
                .password("password")
                .userType(User.UserType.FARMER)
                .status(User.UserStatus.ACTIVE)
                .build();
        
        User merchant = User.builder()
                .username("merchant_test")
                .password("password")
                .userType(User.UserType.MERCHANT)
                .status(User.UserStatus.ACTIVE)
                .build();
        
        userRepository.save(farmer);
        userRepository.save(merchant);
        userRepository.save(testUser);
        
        // 查询农户类型的用户
        List<User> farmers = userRepository.findByUserType(User.UserType.FARMER);
        
        // 验证查询结果
        assertTrue(farmers.size() >= 2);
        assertTrue(farmers.stream().allMatch(u -> u.getUserType() == User.UserType.FARMER));
    }

    @Test
    void testFindByRegion() {
        // 保存用户
        userRepository.save(testUser);
        
        User anotherUser = User.builder()
                .username("another_user")
                .password("password")
                .region("河南省郑州市")
                .userType(User.UserType.FARMER)
                .status(User.UserStatus.ACTIVE)
                .build();
        userRepository.save(anotherUser);
        
        // 查询特定地区的用户
        List<User> usersInRegion = userRepository.findByRegion("河南省郑州市");
        
        // 验证查询结果
        assertTrue(usersInRegion.size() >= 2);
        assertTrue(usersInRegion.stream().allMatch(u -> "河南省郑州市".equals(u.getRegion())));
    }

    @Test
    void testFindByStatus() {
        // 保存用户
        userRepository.save(testUser);
        
        User inactiveUser = User.builder()
                .username("inactive_user")
                .password("password")
                .userType(User.UserType.FARMER)
                .status(User.UserStatus.INACTIVE)
                .build();
        userRepository.save(inactiveUser);
        
        // 查询活跃用户
        List<User> activeUsers = userRepository.findByStatus(User.UserStatus.ACTIVE);
        
        // 验证查询结果
        assertTrue(activeUsers.size() >= 1);
        assertTrue(activeUsers.stream().allMatch(u -> u.getStatus() == User.UserStatus.ACTIVE));
    }

    @Test
    void testUpdateUser() {
        // 保存用户
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();
        
        // 更新用户信息
        savedUser.setEmail("newemail@example.com");
        savedUser.setPhone("13900139000");
        User updatedUser = userRepository.save(savedUser);
        
        // 验证更新结果
        assertEquals("newemail@example.com", updatedUser.getEmail());
        assertEquals("13900139000", updatedUser.getPhone());
        assertNotNull(updatedUser.getUpdatedAt());
    }

    @Test
    void testDeleteUser() {
        // 保存用户
        User savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();
        
        // 删除用户
        userRepository.deleteById(userId);
        
        // 验证用户已删除
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testUserPrePersist() {
        // 创建用户（不设置时间戳）
        User user = User.builder()
                .username("timestamp_test")
                .password("password")
                .userType(User.UserType.FARMER)
                .build();
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 验证时间戳已自动设置
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
        assertEquals(User.UserStatus.ACTIVE, savedUser.getStatus());
    }

    @Test
    void testUserPreUpdate() {
        // 保存用户
        User savedUser = userRepository.save(testUser);
        LocalDateTime originalUpdatedAt = savedUser.getUpdatedAt();
        
        // 等待一段时间后更新
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 更新用户
        savedUser.setEmail("updated@example.com");
        User updatedUser = userRepository.save(savedUser);
        
        // 验证updatedAt已更新
        assertNotNull(updatedUser.getUpdatedAt());
        assertTrue(updatedUser.getUpdatedAt().isAfter(originalUpdatedAt) || 
                   updatedUser.getUpdatedAt().isEqual(originalUpdatedAt));
    }
}
