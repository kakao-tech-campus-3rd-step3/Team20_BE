package com.example.kspot.email.exception;

import java.time.LocalDateTime;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(LocalDateTime expiresAt) {
        super("토큰이 만료되었습니다. 만료시간 : " + expiresAt);
    }
}
