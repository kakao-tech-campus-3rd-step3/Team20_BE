package com.example.kspot.itineraries.controller;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.itineraries.dto.CreateItineraryRequest;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> getItinerary(@PathVariable Long id) {
    ItineraryResponseDto response = itineraryService.getItineraryById(id);
    return ResponseEntity.ok(
        new ApiResponseDto<>(200, "여행 계획 상세 조회 성공", response)
    );
  }


  @PostMapping
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> createItinerary(
      @RequestBody CreateItineraryRequest request
  ){
    Long userdId = 1L; // 현재 UserId를 주지않아서 임시로 사용. 차후에 JWT 토큰으로 변경예정
    ItineraryResponseDto response = itineraryService.createItinerary(request, userdId);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new ApiResponseDto<>(201, "새로운 여행 계획이 생성되었습니다", response)
    );
  }
}
