package com.sharkfitness.util;

import java.util.UUID;

/**
 * Token utility for generating and parsing authentication tokens
 * Token format: UUID_userId
 */
public class TokenUtil {

    /**
     * Generate a unique token for a user
     * @param userId User ID
     * @return Token string in format: UUID_userId
     */
    public static String generateToken(Long userId) {
        return UUID.randomUUID().toString() + "_" + userId;
    }

    /**
     * Extract user ID from token
     * @param token Token string
     * @return User ID
     * @throws IllegalArgumentException if token format is invalid
     */
    public static Long extractUserId(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        String[] parts = token.split("_");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid token format");
        }
        
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID in token");
        }
    }
}
