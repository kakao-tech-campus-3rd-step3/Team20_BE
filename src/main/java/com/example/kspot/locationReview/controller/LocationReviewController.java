package com.example.kspot.locationReview.controller;

import com.example.kspot.auth.jwt.JwtProvider;
import com.example.kspot.contents.dto.PaginationDto;
import com.example.kspot.global.dto.ApiResponseDto;
import com.example.kspot.locationReview.dto.CreateLocationReviewRequest;
import com.example.kspot.locationReview.dto.LocationReviewDto;
import com.example.kspot.locationReview.dto.LocationReviewListDto;
import com.example.kspot.locationReview.entity.LocationReview;
import com.example.kspot.locationReview.service.LocationReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "location_review", description = "장소 관련 리뷰 API")
@RequestMapping("/api/location_review")
@Slf4j
public class LocationReviewController {

  private final LocationReviewService locationReviewService;
  private final JwtProvider jwtProvider;

  @Autowired
  public LocationReviewController(LocationReviewService locationReviewService,
      JwtProvider jwtProvider) {
    this.locationReviewService = locationReviewService;
    this.jwtProvider = jwtProvider;
  }

  @Operation(
      summary = "reviewId를 통한 리뷰 조회",
      description = "reviewId를 이용해 리뷰를 조회합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "리뷰 상세 조회 성공",
          content = @Content(schema = @Schema(implementation = LocationReviewDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 리뷰를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })

  @GetMapping("/{reviewId}")
  public ResponseEntity<ApiResponseDto<LocationReviewDto>> getLocationReview(
      @Parameter(description = "조회할 리뷰의 ID", example = "1")
      @PathVariable Long reviewId) {
    LocationReview review = locationReviewService.findByLocationReviewIdOrThrow(reviewId);
    LocationReviewDto locationReviewDto = LocationReviewDto.fromEntity(review);

    ApiResponseDto<LocationReviewDto> response =
        new ApiResponseDto<>(200, "리뷰 상세 조회 성공", locationReviewDto);
    return ResponseEntity.ok(response);
  }


  @Operation(
      summary = "locationId를 통한 해당장소 리뷰목록 조회",
      description = "locationId를 통해 해당장소의 리뷰 리스트를 불러옵니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "리스트 조회 성공",
          content = @Content(schema = @Schema(implementation = LocationReviewListDto.class))),
      @ApiResponse(responseCode = "404", description = "해당 장소를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })

  @GetMapping("/location/{locationId}")
  public ResponseEntity<ApiResponseDto<LocationReviewListDto>> getLocationReviewList(
      @Parameter(description = "조회할 장소의 locationId", example = "1")
      @PathVariable Long locationId,
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
      @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "페이지 크기", example = "10")
      @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reviewId"));
    Page<LocationReview> reviewPage = locationReviewService.findByLocationIdOrThrow(locationId, pageable);

    // Page<Entity> → List<DTO>
    List<LocationReviewDto> reviewDtoList = reviewPage.getContent()
        .stream()
        .map(LocationReviewDto::fromEntity)
        .collect(Collectors.toList());

    // Pagination 정보 DTO 구성
    PaginationDto pagination = new PaginationDto(
        reviewPage.getNumber(),
        reviewPage.getSize(),
        reviewPage.getTotalElements(),
        reviewPage.getTotalPages()
    );

    // 리스트 DTO 생성
    LocationReviewListDto listDto = new LocationReviewListDto(reviewDtoList, pagination);

    ApiResponseDto<LocationReviewListDto> apiResponse =
        new ApiResponseDto<>(200, "리뷰 목록 조회 성공", listDto);
    return ResponseEntity.ok(apiResponse);
  }


  @Operation(
      summary = "새로운 리뷰 생성",
      description = "사용자의 요청 정보를 기반으로 새로운 리뷰를 생성합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "리뷰 생성 성공",
          content = @Content(schema = @Schema(implementation = LocationReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
      @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PostMapping
  public ResponseEntity<ApiResponseDto<LocationReviewDto>> createLocationReview(
      @RequestBody CreateLocationReviewRequest request,
      HttpServletRequest httpRequest
  ) {
    String token = jwtProvider.extractTokenFromRequest(httpRequest);
    Long userId = jwtProvider.validateToken(token);

    LocationReviewDto response = LocationReviewDto.fromEntity(
        locationReviewService.createReview(request, userId));
    return ResponseEntity.status(HttpStatus.CREATED).body(
        new ApiResponseDto<>(201, "새로운 리뷰가 생성되었습니다", response)
    );
  }

  @Operation(
      summary = "리뷰 수정",
      description = "사용자의 요청 정보를 기반으로 리뷰를 수정합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "리뷰 수정 성공",
          content = @Content(schema = @Schema(implementation = LocationReviewDto.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
      @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PutMapping("/{locationReviewId}")
  public ResponseEntity<ApiResponseDto<LocationReviewDto>> updateLocationReview(
      @PathVariable Long locationReviewId,
      @RequestBody CreateLocationReviewRequest request,
      HttpServletRequest httpRequest
  ) {

    String token = jwtProvider.extractTokenFromRequest(httpRequest);
    Long userId = jwtProvider.validateToken(token);

    log.info("userId = " + userId);
    LocationReviewDto response = LocationReviewDto.fromEntity(
        locationReviewService.updateReview(userId, locationReviewId, request));
    return ResponseEntity.status(HttpStatus.OK)
        .body(new ApiResponseDto<>(200, "리뷰가 업데이트 되었습니다", response));
  }

  @Operation(
      summary = "리뷰 삭제",
      description = "기존에 존재하던 리뷰를 삭제합니다."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
      @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
      @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @DeleteMapping("/{locationReviewId}")
  public ResponseEntity<ApiResponseDto<LocationReviewDto>> deleteLoctionReview(
      @PathVariable Long locationReviewId,
      HttpServletRequest httpRequest) {

    String token = jwtProvider.extractTokenFromRequest(httpRequest);
    Long userId = jwtProvider.validateToken(token);

    if (userId == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
    }
    locationReviewService.deleteReview(userId, locationReviewId);
    return ResponseEntity.ok(new ApiResponseDto<>(200, "리뷰가 정상적으로 삭제되었습니다", null));
  }
}
