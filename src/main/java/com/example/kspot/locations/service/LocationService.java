package com.example.kspot.locations.service;

import com.example.kspot.locations.dto.LocationRequest;
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

    public List<LocationResponse> searchNearby(LocationRequest request) {
        double range = 0.001; // 약 100m

        double minLat = request.latitude() - range;
        double maxLat = request.latitude() + range;
        double minLon = request.longitude() - range;
        double maxLon = request.longitude() + range;

        List<Location> locations = locationRepository
            .findByLatitudeAndLongitudeRange(minLat, maxLat, minLon, maxLon);

        return locations.stream()
            .map(l -> new LocationResponse(
                l.getLocationId(),
                l.getName(),
                l.getAddress(),
                l.getLatitude(),
                l.getLongitude(),
                l.getContentLocations().stream()
                    .map(cl -> new LocationResponse.RelatedContent(
                        cl.getContent().getContent_id(),
                        cl.getContent().getTitle(),
                        cl.getContent().getCategory()
                    ))
                    .toList()
            ))
            .toList();
    }
}
