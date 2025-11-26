package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.AuthResponse;
import com.travelMemory.dto.LoginRequest;
import com.travelMemory.dto.RefreshTokenRequest;
import com.travelMemory.dto.RegisterRequest;
import com.travelMemory.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@Tag(name = "Authentication", description = "User authentication endpoints for registration, login, and token refresh")
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * @param registerRequest the registration request
     * @return ResponseEntity with AuthResponse containing JWT token and user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.travelMemory.common.ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse authResponse = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(authResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Login user with email and password
     * @param loginRequest the login request
     * @return ResponseEntity with AuthResponse containing JWT token and user info
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password, returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.travelMemory.common.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse authResponse = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Login failed: " + e.getMessage()));
        }
    }

    /**
     * Refresh JWT token
     * @param refreshTokenRequest the refresh token request containing expired token
     * @return ResponseEntity with AuthResponse containing new JWT token and user info
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Generate a new JWT token using an expired token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.travelMemory.common.ApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getToken());
            return ResponseEntity.ok(ApiResponse.success(authResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Token refresh failed: " + e.getMessage()));
        }
    }
}
