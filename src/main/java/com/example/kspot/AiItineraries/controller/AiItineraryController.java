package com.example.kspot.AiItineraries.controller;

import com.example.kspot.AiItineraries.dto.AiItineraryListResponse;
import com.example.kspot.AiItineraries.dto.AiItineraryResponse;
import com.example.kspot.AiItineraries.service.AiItineraryService;
import com.example.kspot.auth.jwt.JwtProvider;
import com.example.kspot.global.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-itineraries")
@RequiredArgsConstructor
public class AiItineraryController {

    private final AiItineraryService itineraryService;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    // AI 여행 일정 저장
    @PostMapping
    public ResponseEntity<ApiResponseDto<AiItineraryResponse>> createAiItinerary(
            @RequestBody Map<String, Object> body,
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

    // 유저별 AI 여행 일정 목록 조회
    @GetMapping("/user")
    public ResponseEntity<ApiResponseDto<List<AiItineraryListResponse>>> getUserItineraries(
            HttpServletRequest request,
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

    // AI 여행 일정 단건 조회
    @GetMapping("/{itineraryId}")
    public ResponseEntity<ApiResponseDto<AiItineraryResponse>> getItinerary(
            @PathVariable Long itineraryId,
            HttpServletRequest request
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

    // AI 여행 일정 삭제
    @DeleteMapping("/{itineraryId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteItinerary(
            @PathVariable Long itineraryId,
            HttpServletRequest request
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