package com.example.kspot.locations.service;

import com.example.kspot.locations.entity.LocationImage;
import com.example.kspot.locations.dto.googleapi.GooglePlacesSearchResponse;
import com.example.kspot.locations.dto.googleapi.Place;
import com.example.kspot.locations.dto.googleapi.Photo;
import com.example.kspot.locations.entity.Location;

import com.example.kspot.locations.repository.LocationRepository;
import com.example.kspot.locations.repository.LocationImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LocationImageService {

    private static final Logger logger = LoggerFactory.getLogger(LocationImageService.class);

    private final LocationRepository locationRepository;
    private final LocationImageRepository locationImageRepository;
    private final RestTemplate restTemplate;

    @Value("${google.api.key}")
    private String googleApiKey;

    public LocationImageService(LocationRepository locationRepository,
                                LocationImageRepository locationImageRepository) {
        this.locationRepository = locationRepository;
        this.locationImageRepository = locationImageRepository;
        this.restTemplate =  new RestTemplate();
    }

    /**
     * Google Places API v1 Text Search + Photos API 호출
     * - google_place_id 및 관련 이미지 URL 저장
     */
    public void updateFromGoogleTextSearch(Location location) {
        if (location.getGooglePlaceId() != null && !location.getGooglePlaceId().isBlank()) {
            logger.info("Skipping '{}': already has place_id={}", location.getName(), location.getGooglePlaceId());
            return;
        }

        String query = location.getName() + " " + location.getAddress();
        String searchUrl = "https://places.googleapis.com/v1/places:searchText";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Goog-Api-Key", googleApiKey);
            headers.set("X-Goog-FieldMask", "places.id,places.displayName,places.formattedAddress,places.photos");
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("textQuery", query);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<GooglePlacesSearchResponse> response =
                    restTemplate.postForEntity(searchUrl, request, GooglePlacesSearchResponse.class);

            GooglePlacesSearchResponse resultBody = response.getBody();
            if (resultBody == null || resultBody.getPlaces() == null || resultBody.getPlaces().isEmpty()) {
                logger.info("No results found for '{}'", query);
                return;
            }

            Place place = resultBody.getPlaces().get(0);
            location.setGooglePlaceId(place.getId());
            locationRepository.save(location);
            logger.info("Saved google_place_id={} for '{}'", place.getId(), location.getName());

            // 사진 정보 저장 (최대 3장)
            if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
                for (int i = 0; i < Math.min(place.getPhotos().size(), 3); i++) {
                    Photo photo = place.getPhotos().get(i);
                    String photoName = photo.getName();

                    String mediaUrl = String.format(
                            "https://places.googleapis.com/v1/%s/media?maxHeightPx=1600&skipHttpRedirect=true&key=%s",
                            photoName, googleApiKey
                    );

                    try {
                        HttpHeaders mediaHeaders = new HttpHeaders();
                        mediaHeaders.set("X-Goog-Api-Key", googleApiKey);
                        mediaHeaders.set("Accept", "application/json");
                        HttpEntity<Void> mediaRequest = new HttpEntity<>(mediaHeaders);

                        // photoUri JSON 응답 파싱
                        ResponseEntity<Map> mediaResponse =
                                restTemplate.exchange(mediaUrl, HttpMethod.GET, mediaRequest, Map.class);

                        if (mediaResponse.getStatusCode() == HttpStatus.OK && mediaResponse.getBody() != null) {
                            Object uriObj = mediaResponse.getBody().get("photoUri");
                            if (uriObj != null) {
                                String photoUri = uriObj.toString();

                                LocationImage image = new LocationImage();
                                image.setLocation(location);
                                image.setImageUrl(photoUri);
                                image.setCreatedAt(LocalDateTime.now());
                                locationImageRepository.save(image);

                                logger.info("Saved photo URL for '{}': {}", location.getName(), photoUri);
                            }
                        } else {
                            logger.warn("No photoUri in response for '{}'", photoName);
                        }

                    } catch (Exception e) {
                        logger.warn("Photo API failed for '{}': {}", photoName, e.getMessage());
                    }
                }
            }

        } catch (RestClientException e) {
            logger.error("Google API call failed for '{}': {}", query, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error for '{}': {}", query, e.getMessage());
        }
    }


    /**
     * 테스트용 - 상위 5개 장소 이미지 업데이트
     */
    public void testUpdateFiveLocations() {
        List<Location> locations = locationRepository.findTop5ByGooglePlaceIdIsNull();
        logger.info("Google Places API v1 test started ({} locations)", locations.size());

        for (Location loc : locations) {
            updateFromGoogleTextSearch(loc);
            try {
                Thread.sleep(500); // rate limit 방지
            } catch (InterruptedException ignored) {
            }
        }
    }
}