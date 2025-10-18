package com.example.kspot.users.exception;

public class NotEmailVerifiedException extends RuntimeException {
    public NotEmailVerifiedException(String message) {
        super(message);
    }
}
