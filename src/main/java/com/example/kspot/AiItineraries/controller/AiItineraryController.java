package com.example.kspot.AiItineraries.controller;

import com.example.kspot.AiItineraries.dto.AiItineraryListResponse;
import com.example.kspot.AiItineraries.dto.AiItineraryResponse;
import com.example.kspot.AiItineraries.service.AiItineraryService;
import com.example.kspot.auth.jwt.JwtProvider;
import com.example.kspot.global.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "AI Itinerary", description = "AI 기반 여행 일정 관리 API")
@RestController
@RequestMapping("/api/ai-itineraries")
@RequiredArgsConstructor
public class AiItineraryController {

    private final AiItineraryService itineraryService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Operation(summary = "AI 여행 일정 저장", description = "AI가 생성한 여행 일정을 저장합니다. JWT 인증 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "AI 여행 일정 저장 성공",
                    content = @Content(schema = @Schema(implementation = AiItineraryResponse.class))),
            @ApiResponse(responseCode = "401", description = "JWT 토큰이 유효하지 않습니다"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    // AI 여행 일정 저장
    @PostMapping
    public ResponseEntity<ApiResponseDto<AiItineraryResponse>> createAiItinerary(
            @Parameter(description = "AI가 생성한 전체 JSON 데이터", required = true)
            @RequestBody Map<String, Object> body,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) throws Exception {
        String token = jwtProvider.extractTokenFromRequest(request);
        Long userId = jwtProvider.validateToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
        }

        Map<String, Object> data = (Map<String, Object>) body.get("data");
        Map<String, Object> metadata = (Map<String, Object>) ((Map<?, ?>) data.get("metadata"));

        String startPoint = (String) ((Map<?, ?>) metadata.get("departure")).get("name");
        String endPoint = (String) ((Map<?, ?>) metadata.get("arrival")).get("name");
        String duration = (String) metadata.get("duration");
        String theme = (String) metadata.get("theme");

        Map<String, Object> jsonData = (Map<String, Object>) data;

        AiItineraryResponse response = itineraryService.save(userId, startPoint, endPoint, duration, theme, jsonData);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseDto<>(201, "AI 여행 일정이 성공적으로 저장되었습니다", response));
    }

    @Operation(summary = "AI 여행 일정 목록 조회", description = "로그인한 사용자의 모든 AI 여행 일정을 조회합니다. JWT 인증 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AiItineraryListResponse.class))),
            @ApiResponse(responseCode = "401", description = "JWT 토큰이 유효하지 않습니다"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    // 유저별 AI 여행 일정 목록 조회
    @GetMapping("/user")
    public ResponseEntity<ApiResponseDto<List<AiItineraryListResponse>>> getUserItineraries(
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(description = "true로 설정 시 최신 순 정렬", example = "true")
            @RequestParam(defaultValue = "false") boolean sorted
    ) {
        String token = jwtProvider.extractTokenFromRequest(request);
        Long userId = jwtProvider.validateToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
        }

        List<AiItineraryListResponse> list = itineraryService.getUserItineraries(userId, sorted);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "AI 여행 일정 목록 조회 성공", list));
    }

    @Operation(summary = "AI 여행 일정 단건 조회", description = "특정 AI 여행 일정의 상세 정보를 조회합니다. JWT 인증 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AiItineraryResponse.class))),
            @ApiResponse(responseCode = "401", description = "JWT 토큰이 유효하지 않습니다"),
            @ApiResponse(responseCode = "404", description = "해당 일정을 찾을 수 없음")
    })
    // AI 여행 일정 단건 조회
    @GetMapping("/{itineraryId}")
    public ResponseEntity<ApiResponseDto<AiItineraryResponse>> getItinerary(
            @Parameter(description = "조회할 일정의 ID", example = "1") @PathVariable Long itineraryId,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        String token = jwtProvider.extractTokenFromRequest(request);
        Long userId = jwtProvider.validateToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
        }

        AiItineraryResponse response = itineraryService.getUserItinerary(userId, itineraryId);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "AI 여행 일정 조회 성공", response));
    }

    @Operation(summary = "AI 여행 일정 삭제", description = "특정 AI 여행 일정을 삭제합니다. JWT 인증 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 토큰이 유효하지 않습니다"),
            @ApiResponse(responseCode = "404", description = "해당 일정을 찾을 수 없음")
    })
    // AI 여행 일정 삭제
    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteItinerary(
            @Parameter(description = "삭제할 일정의 ID", example = "1") @PathVariable Long itineraryId,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        String token = jwtProvider.extractTokenFromRequest(request);
        Long userId = jwtProvider.validateToken(token);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(401, "JWT 토큰이 유효하지 않습니다", null));
        }

        itineraryService.deleteItinerary(itineraryId);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "AI 여행 일정이 삭제되었습니다", null));
    }
}