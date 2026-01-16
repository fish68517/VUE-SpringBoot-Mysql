package com.postgraduate.service;

import com.postgraduate.dto.UserProfileDTO;
import com.postgraduate.entity.User;
import com.postgraduate.entity.UserProfile;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.UserProfileRepository;
import com.postgraduate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for UserService.
 * Tests user profile retrieval and update functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private User testUser;
    private UserProfile testUserProfile;

    @BeforeEach
    void setUp() {
        // Clear existing data
        userRepository.deleteAll();

        // Create test user
        testUser = User.builder()
                .username("testuser")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        testUser = userRepository.save(testUser);

        // Create test user profile
        testUserProfile = UserProfile.builder()
                .user(testUser)
                .undergradTier("985")
                .gpa(new BigDecimal("3.50"))
                .gpaScale("4.0")
                .cet4Score(500)
                .cet6Score(450)
                .targetScore(350)
                .otherScores("GRE: 320")
                .build();
        testUserProfile = userProfileRepository.save(testUserProfile);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserProfileSuccess() {
        UserProfileDTO profileDTO = userService.getUserProfile();

        assertNotNull(profileDTO);
        assertEquals(testUser.getId(), profileDTO.getUserId());
        assertEquals("985", profileDTO.getUndergradTier());
        assertEquals(new BigDecimal("3.50"), profileDTO.getGpa());
        assertEquals("4.0", profileDTO.getGpaScale());
        assertEquals(500, profileDTO.getCet4Score());
        assertEquals(450, profileDTO.getCet6Score());
        assertEquals(350, profileDTO.getTargetScore());
        assertEquals("GRE: 320", profileDTO.getOtherScores());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileSuccess() {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .undergradTier("211")
                .gpa(new BigDecimal("3.75"))
                .gpaScale("4.0")
                .cet4Score(550)
                .cet6Score(480)
                .targetScore(380)
                .otherScores("GRE: 330")
                .build();

        UserProfileDTO updatedProfile = userService.updateUserProfile(updateDTO);

        assertNotNull(updatedProfile);
        assertEquals("211", updatedProfile.getUndergradTier());
        assertEquals(new BigDecimal("3.75"), updatedProfile.getGpa());
        assertEquals(550, updatedProfile.getCet4Score());
        assertEquals(480, updatedProfile.getCet6Score());
        assertEquals(380, updatedProfile.getTargetScore());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileWithInvalidGPA() {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .gpa(new BigDecimal("6.0"))
                .build();

        assertThrows(ValidationException.class, () -> {
            userService.updateUserProfile(updateDTO);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileWithInvalidCET4Score() {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .cet4Score(800)
                .build();

        assertThrows(ValidationException.class, () -> {
            userService.updateUserProfile(updateDTO);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileWithInvalidCET6Score() {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .cet6Score(-10)
                .build();

        assertThrows(ValidationException.class, () -> {
            userService.updateUserProfile(updateDTO);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileWithInvalidTargetScore() {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .targetScore(600)
                .build();

        assertThrows(ValidationException.class, () -> {
            userService.updateUserProfile(updateDTO);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileWithInvalidUndergradTier() {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .undergradTier("INVALID")
                .build();

        assertThrows(ValidationException.class, () -> {
            userService.updateUserProfile(updateDTO);
        });
    }

    @Test
    @WithMockUser(username = "nonexistent", roles = "USER")
    void testGetUserProfileWithNonExistentUser() {
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserProfile();
        });
    }

}
