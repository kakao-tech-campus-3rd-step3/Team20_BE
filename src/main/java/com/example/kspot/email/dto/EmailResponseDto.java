package com.example.kspot.email.dto;

public record EmailResponseDto(
        String accessToken,
        String refreshToken
) {
}
