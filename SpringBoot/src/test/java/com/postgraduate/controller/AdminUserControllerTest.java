package com.postgraduate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgraduate.dto.AdminUserDTO;
import com.postgraduate.dto.StatusUpdateRequest;
import com.postgraduate.service.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AdminUserController endpoints.
 * Tests admin user management functionality including listing, filtering, status updates, and password resets.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminUserService adminUserService;

    private AdminUserDTO user1;
    private AdminUserDTO user2;

    @BeforeEach
    void setUp() {
        user1 = AdminUserDTO.builder()
                .id(1L)
                .username("user1")
                .role("USER")
                .status("ENABLED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        user2 = AdminUserDTO.builder()
                .id(2L)
                .username("user2")
                .role("USER")
                .status("DISABLED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllUsersSuccess() throws Exception {
        Page<AdminUserDTO> page = new PageImpl<>(Arrays.asList(user1, user2));
        when(adminUserService.getAllUsers(null, null, 0, null)).thenReturn(page);

        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].username").value("user1"))
                .andExpect(jsonPath("$.data.content[1].username").value("user2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllUsersWithStatusFilter() throws Exception {
        Page<AdminUserDTO> page = new PageImpl<>(Arrays.asList(user1));
        when(adminUserService.getAllUsers(null, "ENABLED", 0, null)).thenReturn(page);

        mockMvc.perform(get("/api/admin/users")
                .param("status", "ENABLED")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].status").value("ENABLED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllUsersWithKeywordFilter() throws Exception {
        Page<AdminUserDTO> page = new PageImpl<>(Arrays.asList(user1));
        when(adminUserService.getAllUsers("user1", null, 0, null)).thenReturn(page);

        mockMvc.perform(get("/api/admin/users")
                .param("keyword", "user1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].username").value("user1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetAllUsersWithPagination() throws Exception {
        Page<AdminUserDTO> page = new PageImpl<>(Arrays.asList(user1), null, 2);
        when(adminUserService.getAllUsers(null, null, 0, 1)).thenReturn(page);

        mockMvc.perform(get("/api/admin/users")
                .param("page", "0")
                .param("size", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    void testGetAllUsersWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetAllUsersWithUserRole() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateUserStatusSuccess() throws Exception {
        AdminUserDTO updatedUser = AdminUserDTO.builder()
                .id(1L)
                .username("user1")
                .role("USER")
                .status("DISABLED")
                .build();

        when(adminUserService.updateUserStatus(1L, "DISABLED")).thenReturn(updatedUser);

        StatusUpdateRequest request = StatusUpdateRequest.builder()
                .status("DISABLED")
                .build();

        mockMvc.perform(patch("/api/admin/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("DISABLED"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testUpdateUserStatusToEnabled() throws Exception {
        AdminUserDTO updatedUser = AdminUserDTO.builder()
                .id(2L)
                .username("user2")
                .role("USER")
                .status("ENABLED")
                .build();

        when(adminUserService.updateUserStatus(2L, "ENABLED")).thenReturn(updatedUser);

        StatusUpdateRequest request = StatusUpdateRequest.builder()
                .status("ENABLED")
                .build();

        mockMvc.perform(patch("/api/admin/users/2/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value("ENABLED"));
    }

    @Test
    void testUpdateUserStatusWithoutAuthentication() throws Exception {
        StatusUpdateRequest request = StatusUpdateRequest.builder()
                .status("DISABLED")
                .build();

        mockMvc.perform(patch("/api/admin/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUpdateUserStatusWithUserRole() throws Exception {
        StatusUpdateRequest request = StatusUpdateRequest.builder()
                .status("DISABLED")
                .build();

        mockMvc.perform(patch("/api/admin/users/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testResetUserPasswordSuccess() throws Exception {
        when(adminUserService.resetUserPassword(1L)).thenReturn("TempPass123!@");

        mockMvc.perform(post("/api/admin/users/1/reset-password")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.tempPassword").value("TempPass123!@"))
                .andExpect(jsonPath("$.data.message").exists());
    }

    @Test
    void testResetUserPasswordWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/admin/users/1/reset-password")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testResetUserPasswordWithUserRole() throws Exception {
        mockMvc.perform(post("/api/admin/users/1/reset-password")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
