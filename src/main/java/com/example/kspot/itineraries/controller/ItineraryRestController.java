package com.example.kspot.itineraries.controller;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.itineraries.dto.CreateItineraryRequest;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.service.ItineraryService;
import com.example.kspot.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> getItinerary(
      @Parameter(description = "조회할 여행 계획의 ID", example = "1")
      @PathVariable Long id) {
    ItineraryResponseDto response = itineraryService.getItineraryById(id);
    return ResponseEntity.ok(
        new ApiResponseDto<>(200, "여행 계획 상세 조회 성공", response)
    );
  }

  @Operation(
      summary = "사용자가 등록한 여행 일정 목록 조회",
      description = "특정 사용자의 ID를 사용해, 해당 사용자가 등록한 모든 일정 조회"
  )
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @GetMapping("/user/{userId}")
  public ResponseEntity<ApiResponseDto<List<ItineraryResponseDto>>> getItinerariesByUserId(
      @Parameter(description = "조회할 사용자 ID")
      @PathVariable Long userId
  ){
    List<ItineraryResponseDto> itineraries = itineraryService.getItinerariesByUserId(userId);
    return ResponseEntity.ok(
        new ApiResponseDto<>(200, "사용자 여행 일정 목록 조회 성공", itineraries)
    );
  }

  @Operation(
      summary = "새로운 여행 계획 생성",
      description = "사용자의 요청 정보를 기반으로 새로운 여행 계획을 생성합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "여행 계획 생성 성공",
          content = @Content(schema = @Schema(implementation = ItineraryResponseDto.class))),
          @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @PostMapping
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> createItinerary(
      @RequestBody CreateItineraryRequest request,
      HttpServletRequest httpRequest
  ) {
    Long userId = jwtProvider.extractUserIdFromRequest(httpRequest);

    ItineraryResponseDto response = itineraryService.createItinerary(request, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new ApiResponseDto<>(201, "새로운 여행 계획이 생성되었습니다", response)
    );
  }

  @Operation(summary = "여행 계획 수정", description = "기존 여행 계획의 정보를 수정합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @PutMapping("/{itineraryId}")
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> updateItinerary(
      @PathVariable Long itineraryId,
      @RequestBody CreateItineraryRequest request,
      HttpServletRequest httpRequest
  ) {
    Long userId = jwtProvider.extractUserIdFromRequest(httpRequest);
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
    }
    ItineraryResponseDto responseDto = itineraryService.updateItinerary(itineraryId, request,
        userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponseDto<>(200, "여행계획이 업데이트 되었습니다", responseDto));
  }

  @Operation(summary = "여행 계획 삭제", description = "여행 계획 ID를 이용해 해당 일정을 삭제합니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
          @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
          @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
          @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
  })
  @DeleteMapping("/{itineraryId}")
  public ResponseEntity<ApiResponseDto<ItineraryResponseDto>> deleteItinerary(
      @PathVariable Long itineraryId,
      HttpServletRequest httpRequest
  ) {
    Long userId = jwtProvider.extractUserIdFromRequest(httpRequest);
    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
    }
    itineraryService.deleteItinerary(itineraryId, userId);
    return ResponseEntity.ok(new ApiResponseDto<>(200, "여행 계획이 정상적으로 삭제되었습니다", null));
  }
}
