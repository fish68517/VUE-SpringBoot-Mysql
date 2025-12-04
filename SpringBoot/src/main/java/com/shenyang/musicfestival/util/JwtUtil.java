package com.shenyang.musicfestival.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token utility for generating and validating tokens
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token
     */
    /**
     * Generate JWT token
     */
    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())  // 修改：增加 set
                .claim("role", role)            // 保持不变：claim 方法在旧版本中也可以直接用
                .setIssuedAt(new Date())        // 修改：增加 set
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // 修改：增加 set
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate refresh token
     */
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())  // 修改这里：增加 set
                .setIssuedAt(new Date())        // 修改这里：增加 set
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration)) // 修改这里：增加 set
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract user ID from token
     */
    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extract role from token
     */
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.get("role", String.class);
    }

}
