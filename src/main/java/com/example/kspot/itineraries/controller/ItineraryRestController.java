package com.example.kspot.itineraries.controller;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.itineraries.dto.CreateItineraryRequest;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.entity.Itinerary;
import com.example.kspot.itineraries.service.ItineraryService;
import com.example.kspot.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Itinerary", description = "여행 일정 관련 API")
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

  @Operation(
          summary = "여행 계획 상세 조회",
          description = "여행 일정 ID를 이용해 특정 여행 계획의 상세 정보를 조회합니다."
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "여행 계획 상세 조회 성공",
                  content = @Content(schema = @Schema(implementation = ItineraryResponseDto.class))),
          @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
          @ApiResponse(responseCode = "404", description = "해당 여행 계획을 찾을 수 없음"),
          @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> getItinerary(@Parameter(description = "조회할 여행 계획의 ID", example = "1")
                                                                             @PathVariable Long id) {
    ItineraryResponseDto response = itineraryService.getItineraryById(id);
    return ResponseEntity.ok(
        new ApiResponseDto<>(200, "여행 계획 상세 조회 성공", response)
    );
  }

  @Operation(
          summary = "새로운 여행 계획 생성",
          description = "사용자의 요청 정보를 기반으로 새로운 여행 계획을 생성합니다."
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "여행 계획 생성 성공",
                  content = @Content(schema = @Schema(implementation = ItineraryResponseDto.class))),
          @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
          @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
          @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<ItineraryResponseDto>> createItinerary(
      @RequestBody CreateItineraryRequest request,
      HttpServletRequest httpRequest
  ){
    Long userId = extractUserIdFromJwt(httpRequest);

    ItineraryResponseDto response = itineraryService.createItinerary(request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new ApiResponseDto<>(201, "새로운 여행 계획이 생성되었습니다", response)
    );
  }

  @PutMapping("/{itinerayId}")
  public ResponseEntity<ApiResponse<ItineraryResponseDto>> updateItinerary(
      @PathVariable Long itinerayId,
      @RequestBody CreateItineraryRequest request,
      HttpServletRequest httpRequest
  ){
    Long userId = extractUserIdFromJwt(httpRequest);
    if(userId == null){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(401, "JWT 토큰이 유효하지 않습니다", null));
    }
    ItineraryResponseDto responseDto = itineraryService.updateItinerary(itinerayId, request, userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponse<>(200, "여행계획이 업데이트 되었습니다", responseDto));
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
