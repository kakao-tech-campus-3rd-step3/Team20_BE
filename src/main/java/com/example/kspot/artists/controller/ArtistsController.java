package com.example.kspot.artists.controller;

import com.example.kspot.artists.service.ArtistsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name="Artists", description = "아티스트 관련 API")
@RestController
@RequestMapping("/api/artists")
public class ArtistsController {

    private final ArtistsService artistsService;

    public ArtistsController(ArtistsService artistsService) {
        this.artistsService = artistsService;
    }

    @Operation(summary = "아티스트 목록 조회", description = "전체 아티스트 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @GetMapping()
    public ResponseEntity<Map<String,Object>> getArtists() {

        Map<String, Object> body = Map.of(
                "status", 200,
                "message", "아티스트 목록 조회 성공",
                "data", Map.of("items", artistsService.getArtists())
        );

        return ResponseEntity.ok(body);
    }


    @Operation(summary = "특정 아티스트 조회",
            description = "아티스트 ID를 이용해 특정 아티스트의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getArtistById(@Parameter(description = "조회할 아티스트의 고유ID", example = "1")
                                                                 @PathVariable Long id) {

        Map<String, Object> body = Map.of(
                "status", 200,
                "message", "아티스트 조회 성공",
                "data", artistsService.getArtistById(id)
        );

        return ResponseEntity.ok(body);
    }

}
