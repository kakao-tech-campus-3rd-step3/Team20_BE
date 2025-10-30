package com.example.kspot.locations.service;

import com.example.kspot.locations.dto.LocationRequest;
import com.example.kspot.locations.dto.LocationResponse;
import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.entity.LocationImage;
import com.example.kspot.locations.exception.LocationNotFoundException;
import com.example.kspot.locations.repository.LocationImageRepository;
import com.example.kspot.locations.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationImageRepository locationImageRepository;

    public LocationService(LocationRepository locationRepository, LocationImageRepository locationImageRepository) {
        this.locationRepository = locationRepository;
        this.locationImageRepository = locationImageRepository;
    }

    public LocationResponse getLocationDetail(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        List<String> imageUrls = locationImageRepository.findByLocation_LocationId(id)
                .stream()
                .map(LocationImage::getImageUrl)
                .collect(Collectors.toList());

        return new LocationResponse(
                location.getLocationId(),
                location.getName(),
                location.getAddress(),
                location.getLatitude(),
                location.getLongitude(),
                List.of(),
                location.getDescription(),
                imageUrls
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
