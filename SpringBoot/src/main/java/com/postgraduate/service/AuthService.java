package com.postgraduate.service;

import com.postgraduate.config.JwtTokenProvider;
import com.postgraduate.dto.LoginRequest;
import com.postgraduate.dto.LoginResponse;
import com.postgraduate.dto.RegisterRequest;
import com.postgraduate.dto.UserDTO;
import com.postgraduate.entity.User;
import com.postgraduate.entity.UserProfile;
import com.postgraduate.exception.ValidationException;
import com.postgraduate.repository.UserProfileRepository;
import com.postgraduate.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling user authentication and registration.
 * Manages user login, registration, and JWT token generation.
 */
@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user account.
     *
     * @param registerRequest the registration request containing username and password
     * @return UserDTO containing the newly created user information
     * @throws ValidationException if username already exists
     */
    @Transactional
    public UserDTO register(RegisterRequest registerRequest) {
        log.info("Registering new user with username: {}", registerRequest.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed: username already exists: {}", registerRequest.getUsername());
            throw new ValidationException("Username already exists");
        }

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .role(User.UserRole.USER)
                .status(User.UserStatus.ENABLED)

                .build();

        user = userRepository.save(user);
        
        // Create empty user profile
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();
        userProfileRepository.save(userProfile);
        
        log.info("User registered successfully with id: {}", user.getId());

        return UserDTO.fromEntity(user);
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param loginRequest the login request containing username and password
     * @return LoginResponse containing user information and JWT token
     * @throws ValidationException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("User login attempt for username: {}", loginRequest.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // Get user details
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new ValidationException("User not found"));

            log.info("User logged in successfully: {}", loginRequest.getUsername());

            return LoginResponse.builder()
                    .user(UserDTO.fromEntity(user))
                    .token(token)
                    .build();

        } catch (Exception ex) {
            log.warn("Login failed for username: {}", loginRequest.getUsername());
            throw new ValidationException("Invalid username or password");
        }
    }

}
