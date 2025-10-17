package com.example.kspot.users.dto;

import com.example.kspot.users.entity.Users;

import java.time.LocalDateTime;

public record UserInfoResponseDto(
        Long id,
        String email,
        String nickname,
        String provider,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean emailVerified
) {
    public static UserInfoResponseDto fromEntity(Users user) {
        return new UserInfoResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getProvider(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isEmailVerified()
        );
    }
}
