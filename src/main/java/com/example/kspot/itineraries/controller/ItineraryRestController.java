package com.example.kspot.itineraries.controller;

import com.example.kspot.contents.dto.ApiResponse;
import com.example.kspot.itineraries.dto.CreateItineraryRequest;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.service.ItineraryService;
import com.example.kspot.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/itineraries")
public class ItineraryRestController {
  private final ItineraryService itineraryService;
  private final JwtProvider jwtProvider;

  @Autowired
  public ItineraryRestController(ItineraryService itineraryService, JwtProvider jwtProvider) {
    this.itineraryService = itineraryService;
    this.jwtProvider = jwtProvider;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ItineraryResponseDto>> getItinerary(@PathVariable Long id) {
    ItineraryResponseDto response = itineraryService.getItineraryById(id);
    return ResponseEntity.ok(
        new ApiResponse<>(200, "여행 계획 상세 조회 성공", response)
    );
  }


  @PostMapping
  public ResponseEntity<ApiResponse<ItineraryResponseDto>> createItinerary(
      @RequestBody CreateItineraryRequest request,
      HttpServletRequest httpRequest
  ){
    Long userId = extractUserIdFromJwt(httpRequest);

    ItineraryResponseDto response = itineraryService.createItinerary(request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new ApiResponse<>(201, "새로운 여행 계획이 생성되었습니다", response)
    );
  }

  @DeleteMapping("/{itineraryId}")
  public ResponseEntity<ApiResponse<ItineraryResponseDto>> updateItinerary(
      @PathVariable Long itineraryId,
      HttpServletRequest httpRequest
  ){
    Long userId = extractUserIdFromJwt(httpRequest);
    if(userId == null){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(401, "JWT 토큰이 유효하지 않습니다", null));
    }
    itineraryService.deleteItinerary(itineraryId, userId);
    return ResponseEntity.ok(new ApiResponse<>(200, "여행 계획이 정상적으로 삭제되었습니다", null));
  }

  private Long extractUserIdFromJwt(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;

    String token = authHeader.substring(7);
    try {
      return jwtProvider.validateToken(token);
    } catch (Exception e) {
      return null;
    }
  }
}
