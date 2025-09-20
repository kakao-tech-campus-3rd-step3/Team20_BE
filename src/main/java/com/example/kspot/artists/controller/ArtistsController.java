package com.example.kspot.artists.controller;

import com.example.kspot.artists.service.ArtistsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/artists")
public class ArtistsController {

    private final ArtistsService artistsService;

    public ArtistsController(ArtistsService artistsService) {
        this.artistsService = artistsService;
    }

    @GetMapping()
    public ResponseEntity<Map<String,Object>> getArtists() {

        Map<String, Object> body = Map.of(
                "status", 200,
                "message", "아티스트 목록 조회 성공",
                "data", Map.of("items", artistsService.getArtists())
        );

        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getArtistById(@PathVariable Long id) {

        Map<String, Object> body = Map.of(
                "status", 200,
                "message", "아티스트 조회 성공",
                "data", artistsService.getArtistById(id)
        );

        return ResponseEntity.ok(body);
    }

}
