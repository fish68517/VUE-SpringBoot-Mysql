package com.sharkfitness.controller;

import com.sharkfitness.dto.LoginRequest;
import com.sharkfitness.dto.RegisterRequest;
import com.sharkfitness.service.AuthService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * Register a new user
     * @param request registration request
     * @return API response with login information
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ApiResponse.success(response);
    }
    
    /**
     * User login
     * @param request login request
     * @return API response with login information
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }
}
