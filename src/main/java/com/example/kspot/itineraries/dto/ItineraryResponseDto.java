package com.example.kspot.itineraries.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ItineraryResponseDto(
    Long itineraryId,
    UserSummaryResponse user,
    String title,
    String description,
    LocalDateTime createdAt,
    List<ItineraryLocationResponse> locations
) { }
