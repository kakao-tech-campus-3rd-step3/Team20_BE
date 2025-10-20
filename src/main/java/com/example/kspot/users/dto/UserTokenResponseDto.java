package com.example.kspot.users.dto;

public record UserTokenResponseDto(
        String accessToken,
        String refreshToken) {}
