package com.example.kspot.artists.controller;

import com.example.kspot.artists.dto.ArtistsResponseDto;
import com.example.kspot.artists.service.ArtistsService;
import com.example.kspot.global.exception.GlobalExceptionHandler.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
public class ArtistsController {

    private final ArtistsService artistsService;

    public ArtistsController(ArtistsService artistsService) {
        this.artistsService = artistsService;
    }

    @GetMapping()
    public ResponseEntity<?> getArtists() {

        List<ArtistsResponseDto> data = artistsService.getArtists();
        ApiResponse<?> body = new ApiResponse<>(200 , "아티스트 목록 조회 성공" , data);

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getArtistById(@PathVariable Long id) {

        ArtistsResponseDto data = artistsService.getArtistById(id);
        ApiResponse<?> body = new ApiResponse<>(200,"아티스트 조회 성공" , data);

        return ResponseEntity.ok(body);
    }

}
