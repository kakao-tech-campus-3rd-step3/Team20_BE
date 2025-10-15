package com.example.kspot.email.exception;

public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException() {
        super("이미 사용된 토큰입니다");
    }
}
