package com.example.kspot.email.exception;

import java.util.Arrays;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(byte[] hash) {
        super("일치하는 토큰이 존재하지 않습니다. hash 값: " + Arrays.toString(hash));
    }
}
