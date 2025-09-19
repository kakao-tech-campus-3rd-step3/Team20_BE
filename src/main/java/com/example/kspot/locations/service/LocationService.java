package com.example.kspot.locations.service;

import com.example.kspot.locations.dto.LocationResponse;
import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.exception.LocationNotFoundException;
import com.example.kspot.locations.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationResponse getLocationDetail(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        return new LocationResponse(
                location.getLocationId(),
                location.getName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                List.of() //related_content 연결 전 빈리스트
        );
    }
}
