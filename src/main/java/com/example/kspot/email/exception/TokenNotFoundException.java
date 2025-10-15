package com.example.kspot.email.exception;

import java.util.Arrays;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String tokenHashHex) {
        super("일치하는 토큰이 존재하지 않습니다. tokenHashHex 값: " + tokenHashHex);
    }
}
