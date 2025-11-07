package com.example.kspot.locations.controller;


import com.example.kspot.locations.service.LocationImageService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationImageController {

    private final LocationImageService locationImageService;

    @PostMapping("/images/update")
    public ResponseEntity<String> updateImagesForEmptyLocations() {
        locationImageService.updateImagesForEmptyLocations();
        return ResponseEntity.ok("Google Places API를 통해 이미지 업데이트를 완료했습니다.");
    }
}