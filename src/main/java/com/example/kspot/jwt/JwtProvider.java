package com.example.kspot.jwt;

import com.example.kspot.users.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access-ttl}")
    private long accessTtl;
    @Value("${jwt.refresh-ttl}")
    private long refreshTtl;

    public String generateAccessToken(Users user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("Email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtl)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public String generateRefreshToken(Users user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUserId().toString())
                .claim("typ", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtl)))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Long validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("토큰 유효성 검사 실패");
        }
    }

}
