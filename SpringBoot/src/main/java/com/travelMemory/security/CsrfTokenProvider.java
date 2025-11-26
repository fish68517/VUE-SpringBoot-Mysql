package com.travelMemory.security;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Provider for generating and validating CSRF tokens.
 * CSRF (Cross-Site Request Forgery) tokens prevent unauthorized requests from external sites.
 */
@Component
public class CsrfTokenProvider {

    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a new CSRF token.
     *
     * @return a random CSRF token
     */
    public String generateToken() {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Validate CSRF token format.
     *
     * @param token the token to validate
     * @return true if token format is valid
     */
    public boolean isValidTokenFormat(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            Base64.getUrlDecoder().decode(token);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
