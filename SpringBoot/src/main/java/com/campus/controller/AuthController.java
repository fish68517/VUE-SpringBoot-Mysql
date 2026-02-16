package com.campus.controller;

import com.campus.dto.*;
import com.campus.entity.User;
import com.campus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user registration, login, token verification and refresh
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user
     * 
     * @param request Registration request
     * @return ApiResponse with registered user information
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with username, email and password")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register endpoint called for username: {}", request.getUsername());
        
        User user = authService.register(request);
        UserDTO userDTO = UserDTO.fromEntity(user);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", userDTO));
    }

    /**
     * Login user and get JWT token
     * 
     * @param request Login request
     * @return ApiResponse with JWT token and user information
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login endpoint called for username: {}", request.getUsername());
        
        LoginResponse loginResponse = authService.login(request);
        
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    /**
     * Verify JWT token validity
     * 
     * @param token JWT token (can be passed as query parameter or Authorization header)
     * @return ApiResponse with token verification result
     */
    @GetMapping("/verify-token")
    @Operation(summary = "Verify JWT token", description = "Check if JWT token is valid and extract claims")
    public ResponseEntity<ApiResponse<TokenVerifyResponse>> verifyToken(
            @RequestParam(value = "token", required = false) String tokenParam,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.debug("Verify token endpoint called");
        
        // Extract token from Authorization header or query parameter
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (tokenParam != null) {
            token = tokenParam;
        }
        
        if (token == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Token is required"));
        }
        
        TokenVerifyResponse response = authService.verifyToken(token);
        
        return ResponseEntity.ok(ApiResponse.success("Token verification completed", response));
    }

    /**
     * Refresh JWT token
     * 
     * @param request Request containing current token
     * @return ApiResponse with new JWT token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT token", description = "Generate a new JWT token using current token")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) TokenRefreshRequest request) {
        
        log.debug("Refresh token endpoint called");
        
        // Extract token from Authorization header or request body
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else if (request != null && request.getToken() != null) {
            token = request.getToken();
        }
        
        if (token == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Token is required"));
        }
        
        String newToken = authService.refreshToken(token);
        
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", newToken));
    }

    /**
     * Logout user (placeholder endpoint)
     * In JWT-based authentication, logout is typically handled on the client side
     * by removing the token from storage
     * 
     * @return ApiResponse with logout success message
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout user (client should remove token from storage)")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Logout endpoint called");

        // Extract token from Authorization header
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Token is required"));
        }

        authService.logout(token);

        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

}
