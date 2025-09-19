package com.example.kspot.locations.controller;

import com.example.kspot.locations.dto.LocationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/locations")
public class LocationRestController {

    private final LocationService locationService;

    public LocationRestController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationDetail(@PathVariable Long id) {
        LocationResponse response = locationService.getLocationDetail(id);
        return ResponseEntity.ok(response);
    }
}
