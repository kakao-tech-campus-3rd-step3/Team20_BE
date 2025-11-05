package com.example.kspot.AiItineraries.dto;

import com.example.kspot.AiItineraries.entity.AiItinerary;

import java.time.LocalDateTime;

//상세 동선 조회용
public record AiItineraryResponse(
        Long itineraryId,
        String startPoint,
        String endPoint,
        String duration,
        String theme,
        String data,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AiItineraryResponse fromEntity(AiItinerary entity) {
        return new AiItineraryResponse(
                entity.getItineraryId(),
                entity.getStartPoint(),
                entity.getEndPoint(),
                entity.getDuration(),
                entity.getTheme(),
                entity.getData(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}