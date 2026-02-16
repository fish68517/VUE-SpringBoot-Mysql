package com.campus.service;

import com.campus.dto.ChangePasswordRequest;
import com.campus.dto.PageResponse;
import com.campus.dto.UpdateProfileRequest;
import com.campus.dto.UpdateUserStatusRequest;
import com.campus.dto.UserDTO;
import com.campus.entity.User;
import com.campus.exception.BusinessException;
import com.campus.repository.UserRepository;
import com.campus.util.JwtUtil;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .phone("13800138000")
                .nickname("Test User")
                .avatarUrl("http://example.com/avatar.jpg")
                .gender("Male")
                .birthday(LocalDate.of(2000, 1, 1))
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        testUser.setId(1L);
    }

    @Test
    void testGetCurrentUserProfileSuccess() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getCurrentUserProfile("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getNickname());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetCurrentUserProfileUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getCurrentUserProfile("nonexistent");
        });

        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateUserProfileSuccess() {
        // Arrange
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nickname("Updated Name")
                .phone("13900139000")
                .gender("Female")
                .birthday(LocalDate.of(2001, 2, 2))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.updateUserProfile("testuser", request);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserProfileUserNotFound() {
        // Arrange
        UpdateProfileRequest request = UpdateProfileRequest.builder()
                .nickname("Updated Name")
                .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUserProfile("nonexistent", request);
        });

        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testChangePasswordSuccess() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        userService.changePassword("testuser", request);

        // Assert
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testChangePasswordNewPasswordsDoNotMatch() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("differentPassword")
                .build();

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword("testuser", request);
        });

        assertEquals(400, exception.getCode());
        assertEquals("New passwords do not match", exception.getMessage());
    }

    @Test
    void testChangePasswordUserNotFound() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword("nonexistent", request);
        });

        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testChangePasswordOldPasswordIncorrect() {
        // Arrange
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword("testuser", request);
        });

        assertEquals(401, exception.getCode());
        assertEquals("Old password is incorrect", exception.getMessage());
    }

    @Test
    void testGetUserByIdSuccess() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        UserDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllUsersSuccess() {
        // Arrange
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .password("hashedPassword")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        user1.setId(1L);

        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .password("hashedPassword")
                .role(User.UserRole.ORGANIZER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        user2.setId(2L);

        List<User> userList = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(0, 10), 2);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Act
        PageResponse<UserDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("user1", result.getContent().get(0).getUsername());
        assertEquals("user2", result.getContent().get(1).getUsername());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testSearchUsersSuccess() {
        // Arrange
        User user1 = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .nickname("Test User")
                .role(User.UserRole.USER)
                .status(User.AccountStatus.ACTIVE)
                .build();
        user1.setId(1L);

        List<User> userList = Arrays.asList(user1);
        Page<User> userPage = new PageImpl<>(userList, PageRequest.of(0, 10), 1);

        when(userRepository.findByKeyword("test", PageRequest.of(0, 10))).thenReturn(userPage);

        // Act
        PageResponse<UserDTO> result = userService.searchUsers("test", PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository, times(1)).findByKeyword("test", PageRequest.of(0, 10));
    }

    @Test
    void testSearchUsersNoResults() {
        // Arrange
        Page<User> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(userRepository.findByKeyword("nonexistent", PageRequest.of(0, 10))).thenReturn(emptyPage);

        // Act
        PageResponse<UserDTO> result = userService.searchUsers("nonexistent", PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        verify(userRepository, times(1)).findByKeyword("nonexistent", PageRequest.of(0, 10));
    }

    @Test
    void testUpdateUserStatusSuccess() {
        // Arrange
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .status(User.AccountStatus.DISABLED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        UserDTO result = userService.updateUserStatus(1L, request);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserStatusUserNotFound() {
        // Arrange
        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder()
                .status(User.AccountStatus.DISABLED)
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUserStatus(999L, request);
        });

        assertEquals(404, exception.getCode());
        assertEquals("User not found", exception.getMessage());
    }

}
