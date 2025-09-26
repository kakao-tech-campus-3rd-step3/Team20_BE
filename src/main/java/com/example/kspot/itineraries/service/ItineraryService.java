package com.example.kspot.itineraries.service;

import com.example.kspot.itineraries.dto.ItineraryLocationResponse;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.dto.UserSummaryResponse;
import com.example.kspot.itineraries.entity.Itinerary;
import com.example.kspot.itineraries.repository.ItineraryRepository;
import com.example.kspot.locations.dto.LocationResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItineraryService {

  private final ItineraryRepository itineraryRepository;

  @Autowired
  public ItineraryService(ItineraryRepository itineraryRepository) {
    this.itineraryRepository = itineraryRepository;
  }

  public ItineraryResponseDto getItineraryById(Long id) {
    Itinerary itinerary = itineraryRepository.findById(id).orElse(null);

    return new ItineraryResponseDto(
        itinerary.getItineraryId(),
        new UserSummaryResponse(
            itinerary.getUserId(),
            null // 아직 User Entity 제대로 구현되어있지 않아 일단 Null
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
