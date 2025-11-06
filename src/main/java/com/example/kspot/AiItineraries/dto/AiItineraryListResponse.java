package com.example.kspot.AiItineraries.dto;

import com.example.kspot.AiItineraries.entity.AiItinerary;

import java.time.LocalDateTime;

//ai 동선 리스트 조회용
public record AiItineraryListResponse(
        Long itineraryId,
        String startPoint,
        String endPoint,
        String duration,
        String theme,
        LocalDateTime createdAt
) {
    public static AiItineraryListResponse fromEntity(AiItinerary entity) {
        return new AiItineraryListResponse(
                entity.getItineraryId(),
                entity.getStartPoint(),
                entity.getEndPoint(),
                entity.getDuration(),
                entity.getTheme(),
                entity.getCreatedAt()
        );
    }
}
