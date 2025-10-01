package com.example.kspot.jwt;

import com.example.kspot.users.entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key")
    private String secretKey;

    public String generateToken(Users user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("name", user.getEmail())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Long validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("토큰 유효성 검사 실패");
        }
    }

}
