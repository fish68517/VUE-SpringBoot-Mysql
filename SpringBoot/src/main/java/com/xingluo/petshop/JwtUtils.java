package com.xingluo.petshop;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    // 密钥，随便写，越复杂越安全
    private static final String SECRET = "XingLuoPetShopSecretKey123456";
    // 过期时间，这里设为 24 小时 (毫秒)
    private static final long EXPIRATION = 24 * 60 * 60 * 1000;


    /**
     * Generate a unique token for a user
     * @param userId User ID
     * @return Token string in format: UUID_userId
     */
    public static String generateToken(Long userId) {
        return SECRET + "_" + userId;
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


    /**
     * 生成 Token
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }



    /**
     * 解析 Token (后续做拦截器鉴权时会用到)
     */
    public static Claims getClaimsByToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}