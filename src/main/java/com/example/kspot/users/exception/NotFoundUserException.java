package com.example.kspot.users.exception;

public class NotFoundUserException extends RuntimeException {
    public NotFoundUserException() {
        super("유저를 찾지 못했습니다");
    }
}
