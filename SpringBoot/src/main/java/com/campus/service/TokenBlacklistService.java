package com.campus.service;

import com.campus.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Token Blacklist Service
 * Manages blacklisted tokens for logout functionality
 */
@Slf4j
@Service
public class TokenBlacklistService {

    @Autowired
    private JwtUtil jwtUtil;

    // In-memory token blacklist (in production, use Redis or database)
    private final Set<String> blacklistedTokens = new HashSet<>();

    /**
     * Add token to blacklist
     * 
     * @param token JWT token to blacklist
     */
    public void blacklistToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        blacklistedTokens.add(token);
        log.info("Token added to blacklist");
    }

    /**
     * Check if token is blacklisted
     * 
     * @param token JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        return blacklistedTokens.contains(token);
    }

    /**
     * Remove token from blacklist (optional, for token reactivation)
     * 
     * @param token JWT token to remove from blacklist
     */
    public void removeFromBlacklist(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        blacklistedTokens.remove(token);
        log.info("Token removed from blacklist");
    }

    /**
     * Clear all blacklisted tokens (for testing or maintenance)
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
        log.info("Token blacklist cleared");
    }

    /**
     * Get the size of blacklist
     * 
     * @return number of blacklisted tokens
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }

}
