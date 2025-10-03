package com.example.kspot.users.dto;

public record UserRequestDto(
        String email,
        String nickname,
        String password
) {
}
