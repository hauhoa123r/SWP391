package org.project.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.project.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtils {
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    @Value("${jwt.secret}")
    private String secretBase64;

    private SecretKey key;

    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(UserDetails userDetails) {
        Long userId = null;
        String role = null;

        if (userDetails instanceof UserEntity userEntity) {
            userId = userEntity.getId();
            role = userEntity.getUserRole().name();
        }

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("userId", userId)
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractClaims(token, claims -> claims.get("userId", Long.class));
    }

    public String extractUserRole(String token) {
        return extractClaims(token, claims -> claims.get("role", String.class));
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
        );
    }
}
