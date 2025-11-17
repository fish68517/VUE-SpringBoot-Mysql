package com.sharkfitness.service;

import com.sharkfitness.dto.LoginRequest;
import com.sharkfitness.dto.RegisterRequest;
import com.sharkfitness.vo.LoginResponse;

/**
 * Service interface for authentication operations
 */
public interface AuthService {
    
    /**
     * Register a new user
     * @param request registration request containing username, password, and role
     * @return LoginResponse with token and user information
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * Authenticate user and generate token
     * @param request login request containing username and password
     * @return LoginResponse with token and user information
     */
    LoginResponse login(LoginRequest request);
}
