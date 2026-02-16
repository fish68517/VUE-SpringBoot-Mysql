package com.campus.controller;

import com.campus.dto.ChangePasswordRequest;
import com.campus.dto.PageResponse;
import com.campus.dto.UpdateProfileRequest;
import com.campus.dto.UpdateUserStatusRequest;
import com.campus.dto.UserDTO;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.service.UserService;
import com.campus.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private String testToken;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJVU0VSIn0.test";

        testUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .phone("13800138000")
                .nickname("Test User")
                .avatarUrl("http://example.com/avatar.jpg")
                .gender("Male")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void testGetCurrentUserProfileSuccess() throws Exception {
        // Arrange
        when(jwtUtil.validateToken(testToken)).thenReturn(true);
        when(jwtUtil.extractUsername(testToken)).thenReturn("testuser");
        when(userService.getCurrentUserProfile("testuser")).thenReturn(testUserDTO);

        // Act & Assert
        mockMvc.perform(get("/users/profile")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService, times(1)).getCurrentUserProfile("testuser");
    }

    @Test
    void testGetCurrentUserProfileMissingToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/users/profile"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testGetCurrentUserProfileInvalidToken() throws Exception {
        // Arrange
        when(jwtUtil.validateToken(testToken)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/users/profile")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testUpdateUserProfileSuccess() throws Exception {
        // Arrange
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nickname("Updated Name")
                .phone("13900139000")
                .gender("Female")
                .birthday(LocalDate.of(2001, 2, 2))
                .build();

        UserDTO updatedUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .phone("13900139000")
                .nickname("Updated Name")
                .gender("Female")
                .birthday(LocalDate.of(2001, 2, 2))
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();

        when(jwtUtil.validateToken(testToken)).thenReturn(true);
        when(jwtUtil.extractUsername(testToken)).thenReturn("testuser");
        when(userService.updateUserProfile("testuser", request)).thenReturn(updatedUserDTO);

        // Act & Assert
        mockMvc.perform(put("/users/profile")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("Updated Name"))
                .andExpect(jsonPath("$.data.phone").value("13900139000"));

        verify(userService, times(1)).updateUserProfile("testuser", request);
    }

    @Test
    void testUpdateUserProfileMissingToken() throws Exception {
        // Arrange
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nickname("Updated Name")
                .build();

        // Act & Assert
        mockMvc.perform(put("/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testChangePasswordSuccess() throws Exception {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(jwtUtil.validateToken(testToken)).thenReturn(true);
        when(jwtUtil.extractUsername(testToken)).thenReturn("testuser");
        doNothing().when(userService).changePassword("testuser", request);

        // Act & Assert
        mockMvc.perform(put("/users/password")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Password changed successfully"));

        verify(userService, times(1)).changePassword("testuser", request);
    }

    @Test
    void testChangePasswordMissingToken() throws Exception {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        // Act & Assert
        mockMvc.perform(put("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testChangePasswordInvalidToken() throws Exception {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(jwtUtil.validateToken(testToken)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(put("/users/password")
                .header("Authorization", "Bearer " + testToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testGetAllUsersSuccess() throws Exception {
        // Arrange
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .nickname("User One")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .nickname("User Two")
                .role(User.UserRole.ORGANIZER)
                .status(User.AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        List<UserDTO> userList = Arrays.asList(user1, user2);
        PageResponse<UserDTO> pageResponse = PageResponse.<UserDTO>builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2L)
                .totalPages(1)
                .isFirst(true)
                .isLast(true)
                .content(userList)
                .build();

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                .andExpect(jsonPath("$.data.content[1].username").value("user2"));

        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    void testSearchUsersSuccess() throws Exception {
        // Arrange
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .nickname("Test User")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();

        List<UserDTO> userList = Arrays.asList(user1);
        PageResponse<UserDTO> pageResponse = PageResponse.<UserDTO>builder()
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .isFirst(true)
                .isLast(true)
                .content(userList)
                .build();

        when(userService.searchUsers("test", PageRequest.of(0, 10))).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/users/search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].username").value("testuser"));

        verify(userService, times(1)).searchUsers("test", PageRequest.of(0, 10));
    }

    @Test
    void testGetUserByIdSuccess() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        // Arrange
        when(userService.getUserById(999L)).thenThrow(new BusinessException(404, "User not found"));

        // Act & Assert
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void testUpdateUserStatusSuccess() throws Exception {
        // Arrange
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .status(User.AccountStatus.DISABLED)
                .build();

        UserDTO disabledUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.DISABLED)
                .build();

        when(userService.updateUserStatus(1L, request)).thenReturn(disabledUserDTO);

        // Act & Assert
        mockMvc.perform(put("/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));

        verify(userService, times(1)).updateUserStatus(1L, request);
    }

    @Test
    void testUpdateUserStatusInvalidRequest() throws Exception {
        // Arrange
        String invalidRequest = "{}";

        // Act & Assert
        mockMvc.perform(put("/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

}
