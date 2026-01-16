package com.postgraduate.service;

import com.postgraduate.dto.FavoriteDTO;
import com.postgraduate.dto.FavoriteStatsDTO;
import com.postgraduate.entity.Favorite;
import com.postgraduate.entity.School;
import com.postgraduate.entity.User;
import com.postgraduate.entity.UserProfile;
import com.postgraduate.exception.ResourceNotFoundException;
import com.postgraduate.repository.FavoriteRepository;
import com.postgraduate.repository.SchoolRepository;
import com.postgraduate.repository.UserProfileRepository;
import com.postgraduate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for FavoriteService.
 * Tests favorite management and statistics calculation functionality.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FavoriteServiceTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private User testUser;
    private School testSchool;
    private UserProfile testUserProfile;

    @BeforeEach
    void setUp() {
        // Clear existing data
        favoriteRepository.deleteAll();
        userProfileRepository.deleteAll();
        userRepository.deleteAll();
        schoolRepository.deleteAll();

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
                .build();
        userProfileRepository.save(testUserProfile);

        // Create test school
        testSchool = School.builder()
                .name("Test University")
                .city("Beijing")
                .tier("985")
                .website("http://test.edu.cn")
                .intro("Test school introduction")
                .deleted(false)
                .build();
        testSchool = schoolRepository.save(testSchool);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testAddFavoriteSuccess() {
        FavoriteDTO favorite = favoriteService.addFavorite(testSchool.getId());

        assertNotNull(favorite);
        assertEquals(testUser.getId(), favorite.getUserId());
        assertEquals(testSchool.getId(), favorite.getSchoolId());
        assertNotNull(favorite.getCreatedAt());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testAddFavoriteWithNonExistentSchool() {
        assertThrows(ResourceNotFoundException.class, () -> {
            favoriteService.addFavorite(999L);
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testAddFavoriteIdempotent() {
        FavoriteDTO favorite1 = favoriteService.addFavorite(testSchool.getId());
        FavoriteDTO favorite2 = favoriteService.addFavorite(testSchool.getId());

        assertEquals(favorite1.getId(), favorite2.getId());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testRemoveFavoriteSuccess() {
        favoriteService.addFavorite(testSchool.getId());
        favoriteService.removeFavorite(testSchool.getId());

        assertFalse(favoriteService.isFavorited(testSchool.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testRemoveFavoriteNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> {
            favoriteService.removeFavorite(testSchool.getId());
        });
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserFavoritesEmpty() {
        Page<FavoriteDTO> favorites = favoriteService.getUserFavorites(0, 20);

        assertNotNull(favorites);
        assertEquals(0, favorites.getTotalElements());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserFavoritesWithData() {
        favoriteService.addFavorite(testSchool.getId());

        Page<FavoriteDTO> favorites = favoriteService.getUserFavorites(0, 20);

        assertNotNull(favorites);
        assertEquals(1, favorites.getTotalElements());
        assertEquals(testSchool.getId(), favorites.getContent().get(0).getSchoolId());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testIsFavoritedTrue() {
        favoriteService.addFavorite(testSchool.getId());

        assertTrue(favoriteService.isFavorited(testSchool.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testIsFavoritedFalse() {
        assertFalse(favoriteService.isFavorited(testSchool.getId()));
    }

    @Test
    void testGetFavoriteStatsWithNoFavorites() {
        FavoriteStatsDTO stats = favoriteService.getFavoriteStats(testSchool.getId());

        assertNotNull(stats);
        assertFalse(stats.getHasData());
        assertEquals(0L, stats.getTotalFavorites());
        assertTrue(stats.getUndergradTierDistribution().isEmpty());
        assertTrue(stats.getCet4BucketDistribution().isEmpty());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetFavoriteStatsWithSingleFavorite() {
        favoriteService.addFavorite(testSchool.getId());

        FavoriteStatsDTO stats = favoriteService.getFavoriteStats(testSchool.getId());

        assertNotNull(stats);
        assertTrue(stats.getHasData());
        assertEquals(1L, stats.getTotalFavorites());
        assertEquals(1L, stats.getUndergradTierDistribution().get("985"));
        assertEquals(1L, stats.getCet4BucketDistribution().get("501-550"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetFavoriteStatsWithMultipleFavorites() {
        // Create additional users with different profiles
        User user2 = User.builder()
                .username("testuser2")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        user2 = userRepository.save(user2);

        UserProfile profile2 = UserProfile.builder()
                .user(user2)
                .undergradTier("211")
                .gpa(new BigDecimal("3.75"))
                .gpaScale("4.0")
                .cet4Score(450)
                .cet6Score(480)
                .targetScore(380)
                .build();
        userProfileRepository.save(profile2);

        User user3 = User.builder()
                .username("testuser3")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        user3 = userRepository.save(user3);

        UserProfile profile3 = UserProfile.builder()
                .user(user3)
                .undergradTier("985")
                .gpa(new BigDecimal("3.80"))
                .gpaScale("4.0")
                .cet4Score(550)
                .cet6Score(500)
                .targetScore(390)
                .build();
        userProfileRepository.save(profile3);

        // Add favorites
        Favorite fav1 = Favorite.builder()
                .userId(testUser.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav1);

        Favorite fav2 = Favorite.builder()
                .userId(user2.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav2);

        Favorite fav3 = Favorite.builder()
                .userId(user3.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav3);

        FavoriteStatsDTO stats = favoriteService.getFavoriteStats(testSchool.getId());

        assertNotNull(stats);
        assertTrue(stats.getHasData());
        assertEquals(3L, stats.getTotalFavorites());
        assertEquals(2L, stats.getUndergradTierDistribution().get("985"));
        assertEquals(1L, stats.getUndergradTierDistribution().get("211"));
        assertEquals(1L, stats.getCet4BucketDistribution().get("425-500"));
        assertEquals(2L, stats.getCet4BucketDistribution().get("501-550"));
    }

    @Test
    void testGetFavoriteStatsWithNonExistentSchool() {
        assertThrows(ResourceNotFoundException.class, () -> {
            favoriteService.getFavoriteStats(999L);
        });
    }

    @Test
    void testGetFavoriteStatsCET4BucketDistribution() {
        // Create users with different CET-4 scores
        User user1 = User.builder()
                .username("user1")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        user1 = userRepository.save(user1);

        UserProfile profile1 = UserProfile.builder()
                .user(user1)
                .undergradTier("985")
                .cet4Score(400)
                .build();
        userProfileRepository.save(profile1);

        User user2 = User.builder()
                .username("user2")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        user2 = userRepository.save(user2);

        UserProfile profile2 = UserProfile.builder()
                .user(user2)
                .undergradTier("985")
                .cet4Score(480)
                .build();
        userProfileRepository.save(profile2);

        User user3 = User.builder()
                .username("user3")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        user3 = userRepository.save(user3);

        UserProfile profile3 = UserProfile.builder()
                .user(user3)
                .undergradTier("985")
                .cet4Score(520)
                .build();
        userProfileRepository.save(profile3);

        User user4 = User.builder()
                .username("user4")
                .passwordHash("hashedpassword")
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)
                .deleted(false)
                .build();
        user4 = userRepository.save(user4);

        UserProfile profile4 = UserProfile.builder()
                .user(user4)
                .undergradTier("985")
                .cet4Score(600)
                .build();
        userProfileRepository.save(profile4);

        // Add favorites
        Favorite fav1 = Favorite.builder()
                .userId(user1.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav1);

        Favorite fav2 = Favorite.builder()
                .userId(user2.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav2);

        Favorite fav3 = Favorite.builder()
                .userId(user3.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav3);

        Favorite fav4 = Favorite.builder()
                .userId(user4.getId())
                .schoolId(testSchool.getId())
                .deleted(false)
                .build();
        favoriteRepository.save(fav4);

        FavoriteStatsDTO stats = favoriteService.getFavoriteStats(testSchool.getId());

        assertNotNull(stats);
        assertEquals(1L, stats.getCet4BucketDistribution().get("<425"));
        assertEquals(1L, stats.getCet4BucketDistribution().get("425-500"));
        assertEquals(1L, stats.getCet4BucketDistribution().get("501-550"));
        assertEquals(1L, stats.getCet4BucketDistribution().get("551+"));
    }
}
