package com.example.kspot.external.tmdb.controller;

import com.example.kspot.external.tmdb.service.TmdbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TMDB", description = "TMDB 데이터 관련 API")
@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @Operation(
            summary = "TMDB 콘텐츠 데이터 수집",
            description = "TMDB Open API를 통해 영화 및 드라마 데이터를 가져와 DB에 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TMDB 데이터 삽입 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public String insert(){
        return tmdbService.insert();
    }

}
