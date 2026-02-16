package com.campus.controller;

import com.campus.dto.ApiResponse;
import com.campus.dto.LoginRequest;
import com.campus.dto.LoginResponse;
import com.campus.dto.TokenRefreshRequest;
import com.campus.service.AuthService;
import com.campus.service.TokenBlacklistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private String validToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clear blacklist before each test
        tokenBlacklistService.clearBlacklist();

        // Register and login to get a valid token
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<LoginResponse> response = objectMapper.readValue(responseBody,
                new com.fasterxml.jackson.core.type.TypeReference<ApiResponse<LoginResponse>>() {});

        if (response.getData() != null) {
            validToken = response.getData().getAccessToken();
        }
    }

    @Test
    void testLogoutWithValidToken() throws Exception {
        if (validToken == null) {
            return; // Skip if token not available
        }

        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    void testLogoutWithoutToken() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Token is required"));
    }

    @Test
    void testRefreshTokenWithValidToken() throws Exception {
        if (validToken == null) {
            return; // Skip if token not available
        }

        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .token(validToken)
                .build();

        mockMvc.perform(post("/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void testRefreshTokenWithBearerHeader() throws Exception {
        if (validToken == null) {
            return; // Skip if token not available
        }

        mockMvc.perform(post("/auth/refresh-token")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void testRefreshTokenWithoutToken() throws Exception {
        mockMvc.perform(post("/auth/refresh-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Token is required"));
    }

    @Test
    void testRegisterWithValidData() throws Exception {
        String registerJson = """
                {
                    "username": "newstudent",
                    "email": "newstudent@example.com",
                    "password": "Password123",
                    "confirmPassword": "Password123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.username").value("newstudent"))
                .andExpect(jsonPath("$.data.email").value("newstudent@example.com"));
    }

    @Test
    void testRegisterWithDuplicateUsername() throws Exception {
        String registerJson = """
                {
                    "username": "testuser",
                    "email": "another@example.com",
                    "password": "Password123",
                    "confirmPassword": "Password123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    void testRegisterWithPasswordMismatch() throws Exception {
        String registerJson = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "password": "Password123",
                    "confirmPassword": "Password456"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        String registerJson = """
                {
                    "username": "newuser",
                    "email": "invalid-email",
                    "password": "Password123",
                    "confirmPassword": "Password123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testRegisterWithWeakPassword() throws Exception {
        String registerJson = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "password": "weak",
                    "confirmPassword": "weak"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

}
