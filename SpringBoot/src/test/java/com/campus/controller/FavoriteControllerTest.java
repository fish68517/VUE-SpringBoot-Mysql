package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.FavoriteDTO;
import com.campus.service.FavoriteService;
import com.campus.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Favorite Controller Test
 */
@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private FavoriteController favoriteController;

    private MockMvc mockMvc;
    private String validToken;
    private FavoriteDTO testFavoriteDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteController).build();
        validToken = "Bearer valid_token";

        testFavoriteDTO = FavoriteDTO.builder()
                .id(1L)
                .userId(1L)
                .activityId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testAddFavorite_Success() {
        // Arrange
        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("testuser");
        when(favoriteService.addFavorite(1L, "testuser")).thenReturn(testFavoriteDTO);

        // Act
        ResponseEntity<ApiResponse<FavoriteDTO>> response = favoriteController.addFavorite(validToken, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Activity added to favorites successfully", response.getBody().getMessage());
        assertEquals(testFavoriteDTO, response.getBody().getData());
        verify(favoriteService, times(1)).addFavorite(1L, "testuser");
    }

    @Test
    void testAddFavorite_InvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid_token";

        // Act
        ResponseEntity<ApiResponse<FavoriteDTO>> response = favoriteController.addFavorite(invalidToken, 1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testAddFavorite_NoToken() {
        // Act
        ResponseEntity<ApiResponse<FavoriteDTO>> response = favoriteController.addFavorite(null, 1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testRemoveFavorite_Success() {
        // Arrange
        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("testuser");
        doNothing().when(favoriteService).removeFavorite(1L, "testuser");

        // Act
        ResponseEntity<ApiResponse<Void>> response = favoriteController.removeFavorite(validToken, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Activity removed from favorites successfully", response.getBody().getMessage());
        verify(favoriteService, times(1)).removeFavorite(1L, "testuser");
    }

    @Test
    void testRemoveFavorite_InvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid_token";

        // Act
        ResponseEntity<ApiResponse<Void>> response = favoriteController.removeFavorite(invalidToken, 1L);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testGetFavorites_Success() {
        // Arrange
        FavoriteDTO favorite2 = FavoriteDTO.builder()
                .id(2L)
                .userId(1L)
                .activityId(2L)
                .createdAt(LocalDateTime.now())
                .build();

        Page<FavoriteDTO> favoritePage = new PageImpl<>(Arrays.asList(testFavoriteDTO, favorite2));

        when(jwtUtil.validateToken("valid_token")).thenReturn(true);
        when(jwtUtil.extractUsername("valid_token")).thenReturn("testuser");
        when(favoriteService.getFavorites("testuser", 0, 10)).thenReturn(favoritePage);

        // Act
        ResponseEntity<ApiResponse<Page<FavoriteDTO>>> response = favoriteController.getFavorites(validToken, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Favorites retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().getContent().size());
        verify(favoriteService, times(1)).getFavorites("testuser", 0, 10);
    }

    @Test
    void testGetFavorites_InvalidToken() {
        // Arrange
        String invalidToken = "Bearer invalid_token";

        // Act
        ResponseEntity<ApiResponse<Page<FavoriteDTO>>> response = favoriteController.getFavorites(invalidToken, 0, 10);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

    @Test
    void testGetFavorites_NoToken() {
        // Act
        ResponseEntity<ApiResponse<Page<FavoriteDTO>>> response = favoriteController.getFavorites(null, 0, 10);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid or missing token", response.getBody().getMessage());
    }

}
