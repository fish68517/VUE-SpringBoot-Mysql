package com.postgraduate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgraduate.dto.UserProfileDTO;
import com.postgraduate.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for UserController endpoints.
 * Tests user profile retrieval and update functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserProfileDTO userProfileDTO;

    @BeforeEach
    void setUp() {
        userProfileDTO = UserProfileDTO.builder()
                .userId(1L)
                .undergradTier("985")
                .gpa(new BigDecimal("3.50"))
                .gpaScale("4.0")
                .cet4Score(500)
                .cet6Score(450)
                .targetScore(350)
                .otherScores("GRE: 320")
                .build();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetUserProfileSuccess() throws Exception {
        when(userService.getUserProfile()).thenReturn(userProfileDTO);

        mockMvc.perform(get("/api/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.undergradTier").value("985"))
                .andExpect(jsonPath("$.data.gpa").value(3.50))
                .andExpect(jsonPath("$.data.cet4Score").value(500));
    }

    @Test
    void testGetUserProfileWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testUpdateUserProfileSuccess() throws Exception {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .undergradTier("211")
                .gpa(new BigDecimal("3.75"))
                .gpaScale("4.0")
                .cet4Score(550)
                .cet6Score(480)
                .targetScore(380)
                .build();

        UserProfileDTO updatedProfile = UserProfileDTO.builder()
                .userId(1L)
                .undergradTier("211")
                .gpa(new BigDecimal("3.75"))
                .gpaScale("4.0")
                .cet4Score(550)
                .cet6Score(480)
                .targetScore(380)
                .build();

        when(userService.updateUserProfile(any(UserProfileDTO.class))).thenReturn(updatedProfile);

        mockMvc.perform(put("/api/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.undergradTier").value("211"))
                .andExpect(jsonPath("$.data.gpa").value(3.75))
                .andExpect(jsonPath("$.data.cet4Score").value(550));
    }

    @Test
    void testUpdateUserProfileWithoutAuthentication() throws Exception {
        UserProfileDTO updateDTO = UserProfileDTO.builder()
                .undergradTier("211")
                .build();

        mockMvc.perform(put("/api/me/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isUnauthorized());
    }

}
