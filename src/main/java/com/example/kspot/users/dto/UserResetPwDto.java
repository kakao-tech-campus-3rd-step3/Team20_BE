package com.example.kspot.users.dto;

public record UserResetPwDto(
        String rawToken,
        String password
) {
}
