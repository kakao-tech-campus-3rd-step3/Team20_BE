package com.example.kspot.itineraries.controller;

import com.example.kspot.contents.dto.ApiResponse;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.service.ItineraryService;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/itineraries")
public class ItineraryRestController {
  private final ItineraryService itineraryService;

  @Autowired
  public ItineraryRestController(ItineraryService itineraryService) {
    this.itineraryService = itineraryService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ItineraryResponseDto>> getItinerary(@PathVariable Long id) {
    ItineraryResponseDto response = itineraryService.getItineraryById(id);
    return ResponseEntity.ok(
        new ApiResponse<>(200, "여행 계획 상세 조회 성공", response)
    );
  }
}
