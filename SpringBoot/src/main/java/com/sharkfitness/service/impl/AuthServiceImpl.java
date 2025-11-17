package com.sharkfitness.service.impl;

import com.sharkfitness.dto.LoginRequest;
import com.sharkfitness.dto.RegisterRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.AuthService;
import com.sharkfitness.util.TokenUtil;
import com.sharkfitness.vo.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of authentication service
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(400, "Username already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());  // Plain text storage as per requirements
        user.setRole(request.getRole());
        
        // Save user
        user = userRepository.save(user);
        
        // Generate token
        String token = TokenUtil.generateToken(user.getId());
        
        // Return login response
        return new LoginResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getAvatar()
        );
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
        
        // Compare plain text password
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }
        
        // Generate token
        String token = TokenUtil.generateToken(user.getId());
        
        // Return login response
        return new LoginResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getRole(),
            user.getAvatar()
        );
    }
}
