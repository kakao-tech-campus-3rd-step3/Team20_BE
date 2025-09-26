package com.example.kspot.itineraries.dto;

import java.util.List;

public record CreateItineraryRequest(
    String title,
    String description,
    List<LocationRequest> locations
) {
  public record LocationRequest(
      Long locationId,
      int visitOrder
  ){}
}
