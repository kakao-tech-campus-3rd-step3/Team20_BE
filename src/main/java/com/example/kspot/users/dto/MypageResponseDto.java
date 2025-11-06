package com.example.kspot.users.dto;

import com.example.kspot.AiItineraries.dto.AiItineraryListResponse;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;

import java.util.List;

public record MypageResponseDto(
        String email,
        String nickname,
        List<AiItineraryListResponse> aiList,
        List<ItineraryResponseDto> list
) {
}
