package com.submission.service;

import com.submission.dto.UserDTO;
import com.submission.entity.User;
import com.submission.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * User Service Test - Tests for user authentication and registration
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .phone("1234567890")
                .role("AUTHOR")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .phone("1234567890")
                .role("AUTHOR")
                .status("ACTIVE")
                .build();
    }

    @Test
    void testRegisterNewUser() {
        when(userMapper.findByUsername("testuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(userDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertEquals("PENDING", result.getStatus());
        verify(userMapper, times(1)).findByUsername("testuser");
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(userMapper.findByUsername("testuser")).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userService.register(userDTO));
        verify(userMapper, times(1)).findByUsername("testuser");
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        when(userMapper.findByUsername("testuser")).thenReturn(user);

        User result = userService.login("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("AUTHOR", result.getRole());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoginUserNotFound() {
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.login("nonexistent", "password123"));
        verify(userMapper, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testLoginInvalidPassword() {
        when(userMapper.findByUsername("testuser")).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userService.login("testuser", "wrongpassword"));
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoginUserNotApproved() {
        User pendingUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password123")
                .role("AUTHOR")
                .status("PENDING")
                .build();

        when(userMapper.findByUsername("testuser")).thenReturn(pendingUser);

        assertThrows(RuntimeException.class, () -> userService.login("testuser", "password123"));
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetUserById() {
        when(userMapper.findById(1L)).thenReturn(user);

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    void testGetUserByUsername() {
        when(userMapper.findByUsername("testuser")).thenReturn(user);

        User result = userService.getUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).findByUsername("testuser");
    }

    @Test
    void testUpdateUser() {
        userDTO.setId(1L);
        userDTO.setEmail("newemail@example.com");

        when(userMapper.update(any(User.class))).thenReturn(1);

        User result = userService.updateUser(userDTO);

        assertNotNull(result);
        assertEquals("newemail@example.com", result.getEmail());
        verify(userMapper, times(1)).update(any(User.class));
    }
}
