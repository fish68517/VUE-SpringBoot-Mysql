package com.postgraduate.service;

import com.postgraduate.dto.AdminUserDTO;
import com.postgraduate.entity.User;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for AdminUserService.
 * Tests admin user management functionality including listing, filtering, status updates, and password resets.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminUserServiceTest {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private UserRepository userRepository;

    private User enabledUser;
    private User disabledUser;

    @BeforeEach
    void setUp() {
        // Clear existing data
        userRepository.deleteAll();

        // Create enabled test user
        enabledUser = User.builder()
                .username("enableduser")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        enabledUser = userRepository.save(enabledUser);

        // Create disabled test user
        disabledUser = User.builder()
                .username("disableduser")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.DISABLED)
                .deleted(false)
                .build();
        disabledUser = userRepository.save(disabledUser);
    }

    @Test
    void testGetAllUsersWithoutFilters() {
        Page<AdminUserDTO> users = adminUserService.getAllUsers(null, null, 0, null);

        assertNotNull(users);
        assertEquals(2, users.getTotalElements());
        assertEquals(2, users.getNumberOfElements());
    }

    @Test
    void testGetAllUsersFilterByStatus() {
        Page<AdminUserDTO> enabledUsers = adminUserService.getAllUsers(null, "ENABLED", 0, null);

        assertNotNull(enabledUsers);
        assertEquals(1, enabledUsers.getTotalElements());
        assertEquals("enableduser", enabledUsers.getContent().get(0).getUsername());
        assertEquals("ENABLED", enabledUsers.getContent().get(0).getStatus());
    }

    @Test
    void testGetAllUsersFilterByKeyword() {
        Page<AdminUserDTO> users = adminUserService.getAllUsers("enabled", null, 0, null);

        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
        assertEquals("enableduser", users.getContent().get(0).getUsername());
    }

    @Test
    void testGetAllUsersFilterByStatusAndKeyword() {
        Page<AdminUserDTO> users = adminUserService.getAllUsers("enabled", "ENABLED", 0, null);

        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
        assertEquals("enableduser", users.getContent().get(0).getUsername());
    }

    @Test
    void testGetAllUsersWithPagination() {
        Page<AdminUserDTO> users = adminUserService.getAllUsers(null, null, 0, 1);

        assertNotNull(users);
        assertEquals(2, users.getTotalElements());
        assertEquals(1, users.getNumberOfElements());
        assertEquals(2, users.getTotalPages());
    }

    @Test
    void testUpdateUserStatusToDisabled() {
        AdminUserDTO updatedUser = adminUserService.updateUserStatus(enabledUser.getId(), "DISABLED");

        assertNotNull(updatedUser);
        assertEquals("DISABLED", updatedUser.getStatus());

        // Verify in database
        User user = userRepository.findById(enabledUser.getId()).orElseThrow();
        assertEquals(User.UserStatus.DISABLED, user.getStatus());
    }

    @Test
    void testUpdateUserStatusToEnabled() {
        AdminUserDTO updatedUser = adminUserService.updateUserStatus(disabledUser.getId(), "ENABLED");

        assertNotNull(updatedUser);
        assertEquals("ENABLED", updatedUser.getStatus());

        // Verify in database
        User user = userRepository.findById(disabledUser.getId()).orElseThrow();
        assertEquals(User.UserStatus.ENABLED, user.getStatus());
    }

    @Test
    void testUpdateUserStatusWithInvalidStatus() {
        assertThrows(ValidationException.class, () -> {
            adminUserService.updateUserStatus(enabledUser.getId(), "INVALID");
        });
    }

    @Test
    void testUpdateUserStatusWithNonExistentUser() {
        assertThrows(ResourceNotFoundException.class, () -> {
            adminUserService.updateUserStatus(999L, "DISABLED");
        });
    }

    @Test
    void testResetUserPassword() {
        String tempPassword = adminUserService.resetUserPassword(enabledUser.getId());

        assertNotNull(tempPassword);
        assertFalse(tempPassword.isEmpty());
        assertEquals(12, tempPassword.length());

        // Verify password was changed in database
        User user = userRepository.findById(enabledUser.getId()).orElseThrow();
        assertNotEquals("hashedpassword", user.getPasswordHash());
    }

    @Test
    void testResetUserPasswordWithNonExistentUser() {
        assertThrows(ResourceNotFoundException.class, () -> {
            adminUserService.resetUserPassword(999L);
        });
    }

    @Test
    void testResetUserPasswordGeneratesDifferentPasswords() {
        String tempPassword1 = adminUserService.resetUserPassword(enabledUser.getId());
        String tempPassword2 = adminUserService.resetUserPassword(disabledUser.getId());

        assertNotNull(tempPassword1);
        assertNotNull(tempPassword2);
        assertNotEquals(tempPassword1, tempPassword2);
    }

}
