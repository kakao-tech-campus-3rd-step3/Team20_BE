package com.example.kspot.locations.controller;

import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.repository.LocationRepository;
import com.example.kspot.locations.service.LocationImageService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationImageController {

    private final LocationImageService locationImageService;

    // http://localhost:8080/api/locations/images/update
    @PostMapping("/images/update")
    public ResponseEntity<String> updateImagesForEmptyLocations() {
        locationImageService.updateImagesForEmptyLocations();
        return ResponseEntity.ok("Google Places API를 통해 이미지 500장 업데이트를 완료했습니다.");
    }
}