package com.example.kspot.itineraries.dto;

import com.example.kspot.itineraries.entity.Itinerary;
import java.time.LocalDateTime;
import java.util.List;

public record ItineraryResponseDto(
    Long itineraryId,
    UserSummaryResponse user,
    String title,
    String description,
    LocalDateTime createdAt,
    List<ItineraryLocationResponse> locations
) {
  public static ItineraryResponseDto fromEntity(Itinerary itinerary) {
    return new ItineraryResponseDto(
        itinerary.getItineraryId(),
        new UserSummaryResponse(
            itinerary.getUser().getUserId(),
            itinerary.getUser().getNickname()
        ),
        itinerary.getTitle(),
        itinerary.getDescription(),
        itinerary.getCreated_at(),
        itinerary.getItineraryLocations().stream()
            .map(il -> new ItineraryLocationResponse(
                il.getLocation().getLocationId(),
                il.getLocation().getName(),
                il.getLocation().getAddress(),
                il.getVisitOrder()
            )).toList()
    );
  }
}
