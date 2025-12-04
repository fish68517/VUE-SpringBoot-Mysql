package com.shenyang.musicfestival.service;

import com.shenyang.musicfestival.dto.UserDTO;
import com.shenyang.musicfestival.entity.User;
import com.shenyang.musicfestival.repository.UserRepository;
import com.shenyang.musicfestival.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for UserService
 */
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        userRepository.deleteAll();

        // Create a test user
        testUser = userService.register("13800138000", "password123");
    }

    @Test
    void testVerifyRealName() {
        String realName = "张三";
        String idNumber = "110101199003071234";

        User verifiedUser = userService.verifyRealName(testUser.getId(), realName, idNumber);

        assertNotNull(verifiedUser);
        assertEquals(realName, verifiedUser.getRealName());
        assertEquals(idNumber, verifiedUser.getIdNumber());
        assertTrue(verifiedUser.getIsRealNameVerified());
    }

    @Test
    void testIdNumberExists() {
        String idNumber = "110101199003071234";
        userService.verifyRealName(testUser.getId(), "张三", idNumber);

        assertTrue(userService.idNumberExists(idNumber));
    }

    @Test
    void testIdNumberNotExists() {
        assertFalse(userService.idNumberExists("999999999999999999"));
    }

    @Test
    void testFindByIdNumber() {
        String idNumber = "110101199003071234";
        userService.verifyRealName(testUser.getId(), "张三", idNumber);

        var foundUser = userService.findByIdNumber(idNumber);
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
    }

    @Test
    void testGetUserById() {
        var foundUser = userService.getUserById(testUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getPhone(), foundUser.get().getPhone());
    }

    @Test
    void testGetUserByIdNotFound() {
        var foundUser = userService.getUserById(99999L);
        assertFalse(foundUser.isPresent());
    }

}
