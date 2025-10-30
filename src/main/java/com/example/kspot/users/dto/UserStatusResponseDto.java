package com.example.kspot.users.dto;

import java.time.Instant;

public record UserStatusResponseDto(
        boolean isLoggedIn,
        Long userId
) {
}
