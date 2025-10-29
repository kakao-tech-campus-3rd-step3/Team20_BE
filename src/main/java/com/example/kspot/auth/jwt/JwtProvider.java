package com.example.kspot.auth.jwt;

import com.example.kspot.email.exception.TokenNotFoundException;
import com.example.kspot.users.entity.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;
  @Value("${jwt.access-ttl}")
  private long accessTtl;
  @Value("${jwt.refresh-ttl}")
  private long refreshTtl;

  private static final String[] COOKIE_NAMES = {
      "__Host-access_token",
      "__Host-refresh_token",
      "access_token",
      "refresh_token"
  };

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

  public String extractTokenFromRequest(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && !authHeader.isBlank()) {
      return authHeader.substring(7).trim();
    }

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie c : cookies) {
        String name = c.getName();
        for (String candidate : COOKIE_NAMES) {
          if (candidate.equalsIgnoreCase(name)) {
            String value = c.getValue();
            if (value != null && !value.isBlank()) {
              return value.trim();
            }
          }
        }
      }
    }

    throw new TokenNotFoundException("Authorization header is invalid");
  }

  public Instant getExpiration(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
          .build()
          .parseSignedClaims(token)
          .getPayload();

      return claims.getExpiration().toInstant();
    } catch (Exception e) {
      throw new RuntimeException("토큰 유효성 검사 실패");
    }
  }
}
