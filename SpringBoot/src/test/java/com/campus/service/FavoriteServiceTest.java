package com.campus.service;

import com.campus.dto.FavoriteDTO;
import com.campus.entity.Activity;
import com.campus.entity.Favorite;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.ActivityRepository;
import com.campus.repository.FavoriteRepository;
import com.campus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Favorite Service Test
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private User testUser;
    private Activity testActivity;
    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        testUser.setId(1L);

        // Create test activity
        testActivity = Activity.builder()
                .organizerId(2L)
                .title("Test Activity")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .status(Activity.ActivityStatus.APPROVED)
                .enableCrowdfunding(false)
                .build();
        testActivity.setId(1L);

        // Create test favorite
        testFavorite = Favorite.builder()
                .userId(testUser.getId())
                .activityId(testActivity.getId())
                .build();
        testFavorite.setId(1L);
    }

    @Test
    void testAddFavorite_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 1L)).thenReturn(false);
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testFavorite);

        // Act
        FavoriteDTO result = favoriteService.addFavorite(1L, "testuser");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(1L, result.getActivityId());
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    void testAddFavorite_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.addFavorite(1L, "nonexistent");
        });
        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testAddFavorite_ActivityNotFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.addFavorite(999L, "testuser");
        });
        assertEquals(404, exception.getCode());
        assertEquals("Activity not found", exception.getMessage());
    }

    @Test
    void testAddFavorite_AlreadyFavorited() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 1L)).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.addFavorite(1L, "testuser");
        });
        assertEquals(409, exception.getCode());
        assertEquals("Activity is already in your favorites", exception.getMessage());
    }

    @Test
    void testRemoveFavorite_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 1L)).thenReturn(true);

        // Act
        favoriteService.removeFavorite(1L, "testuser");

        // Assert
        verify(favoriteRepository, times(1)).deleteByUserIdAndActivityId(1L, 1L);
    }

    @Test
    void testRemoveFavorite_NotFavorited() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(activityRepository.findById(1L)).thenReturn(Optional.of(testActivity));
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 1L)).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.removeFavorite(1L, "testuser");
        });
        assertEquals(404, exception.getCode());
        assertEquals("Activity is not in your favorites", exception.getMessage());
    }

    @Test
    void testGetFavorites_Success() {
        // Arrange
        Favorite favorite2 = Favorite.builder()
                .userId(testUser.getId())
                .activityId(2L)
                .build();
        favorite2.setId(2L);

        Page<Favorite> favoritePage = new PageImpl<>(Arrays.asList(testFavorite, favorite2));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(favoriteRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(favoritePage);

        // Act
        Page<FavoriteDTO> result = favoriteService.getFavorites("testuser", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());
    }

    @Test
    void testGetFavorites_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            favoriteService.getFavorites("nonexistent", 0, 10);
        });
        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testIsFavorited_True() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 1L)).thenReturn(true);

        // Act
        boolean result = favoriteService.isFavorited(1L, "testuser");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsFavorited_False() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(favoriteRepository.existsByUserIdAndActivityId(1L, 1L)).thenReturn(false);

        // Act
        boolean result = favoriteService.isFavorited(1L, "testuser");

        // Assert
        assertFalse(result);
    }

}
