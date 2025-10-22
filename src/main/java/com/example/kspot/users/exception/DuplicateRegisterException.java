package com.example.kspot.users.exception;

public class DuplicateRegisterException extends RuntimeException {
    public DuplicateRegisterException() {
        super("이미 회원가입 되어있습니다!");
    }
}
