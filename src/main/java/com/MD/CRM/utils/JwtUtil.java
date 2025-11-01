package com.MD.CRM.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtUtil {

    @NonFinal
    @Value("${jwt.secret-key}")
    protected String JWT_SECRET_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long JWT_VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long JWT_REFRESHABLE_DURATION;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }

    /**
     * Sinh token mới
     */
    public String generateToken(String email, boolean isRefresh) {
        long duration = isRefresh ? JWT_REFRESHABLE_DURATION : JWT_VALID_DURATION;
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(duration, ChronoUnit.SECONDS)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Kiểm tra token hợp lệ
     */
    public boolean isTokenValid(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Lấy email (subject) từ token
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Lấy thời gian hết hạn token
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Kiểm tra token hết hạn chưa
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Parse và lấy toàn bộ claims từ token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Xác thực token và trả về claims (nếu cần)
     */
    public Claims verifyToken(String token) {
        try {
            return extractAllClaims(token);
        } catch (JwtException e) {
            throw new JwtException("Invalid or expired token");
        }
    }
}
