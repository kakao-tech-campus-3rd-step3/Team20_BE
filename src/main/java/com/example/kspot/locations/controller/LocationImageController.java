package com.example.kspot.locations.controller;

import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.repository.LocationRepository;
import com.example.kspot.locations.service.LocationImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationImageController {

    private final LocationImageService locationImageService;
    private final LocationRepository locationRepository;

    // http://localhost:8080/api/locations/google-test
    @PostMapping("/google-test")
    public String googleSyncTest() {
        List<Location> targets = locationRepository.findTop5ByGooglePlaceIdIsNull();
        int updated = 0;
        for (Location loc : targets) {
            locationImageService.updateFromGoogleTextSearch(loc);
            updated++;
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
        return "Updated " + updated + " locations.";
    }
}