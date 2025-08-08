package com.ecommerce.server_side.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;
    private final long EXPIRATION = 1000 * 60 * 60 * 10;

    @PostConstruct
    public void init() {
        log.info("Initializing JWT with secret length: {} characters", secret.length());
        
        // Ensure the secret is at least 256 bits (32 bytes)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        log.info("Key bytes length: {} bytes", keyBytes.length);
        
        if (keyBytes.length < 32) {
            log.warn("JWT secret is too short ({} bytes), padding to 32 bytes", keyBytes.length);
            // Pad the key if it's too short
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            this.key = Keys.hmacShaKeyFor(paddedKey);
        } else {
            log.info("JWT secret is sufficient length ({} bytes)", keyBytes.length);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
        
        log.info("JWT initialization completed successfully");
    }

    public String generateAccessToken(String username, Long userId) {
        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .claim("userId", userId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 mins
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.info("Generated access token for user: {}", username);
            return token;
        } catch (Exception e) {
            log.error("Error generating access token for user: {}", username, e);
            throw e;
        }
    }

    public String generateRefreshToken(String username) {
        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.info("Generated refresh token for user: {}", username);
            return token;
        } catch (Exception e) {
            log.error("Error generating refresh token for user: {}", username, e);
            throw e;
        }
    }

    public String extractUsername(String token){
        try {
            String username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            log.debug("Extracted username from token: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Error extracting username from token", e);
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = claims.get("userId", Long.class);
            log.debug("Extracted userId from token: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Error extracting userId from token", e);
            return null;
        }
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.debug("Token validation successful");
            return true;
        }catch(Exception e){
            log.error("Token validation failed", e);
            return false;
        }
    }
}
