package com.example.kspot.users.exception;

public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException() {
        super("이미 사용중인 닉네임 입니다.");
    }
}
