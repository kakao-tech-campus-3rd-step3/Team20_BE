package com.example.kspot.itineraries.dto;

import com.example.kspot.locations.dto.LocationResponse;

public record ItineraryLocationResponse(
    Long locationId,
    String name,
    String address,
    int visitOrder
) {}
