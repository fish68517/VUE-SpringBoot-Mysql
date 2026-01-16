package com.postgraduate.controller;

import com.postgraduate.dto.ApiResponse;
import com.postgraduate.dto.LoginRequest;
import com.postgraduate.dto.LoginResponse;
import com.postgraduate.dto.RegisterRequest;
import com.postgraduate.dto.UserDTO;
import com.postgraduate.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for authentication endpoints.
 * Handles user registration, login, and logout operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user account.
     *
     * @param registerRequest the registration request containing username and password
     * @return ResponseEntity with ApiResponse containing the newly created user
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register endpoint called for username: {}", registerRequest.getUsername());
        UserDTO userDTO = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", userDTO));
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param loginRequest the login request containing username and password
     * @return ResponseEntity with ApiResponse containing user information and JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login endpoint called for username: {}", loginRequest.getUsername());
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    /**
     * Logout the current authenticated user.
     * Since the system uses stateless JWT authentication, this endpoint
     * signals to the frontend to clear the stored token.
     *
     * @return ResponseEntity with ApiResponse indicating successful logout
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("Logout endpoint called");
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }

}
