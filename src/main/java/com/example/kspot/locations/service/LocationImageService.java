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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class LocationImageService {

    private static final Logger logger = LoggerFactory.getLogger(LocationImageService.class);

    private final LocationRepository locationRepository;
    private final LocationImageRepository locationImageRepository;
    private final RestTemplate restTemplate;

    double MIN_HORIZONTAL_ASPECT_RATIO = 1.45;
    double MAX_HORIZONTAL_ASPECT_RATIO = 1.85;

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

            // 사진 정보 저장 (가로 이미지 1장 선별)
            if (place.getPhotos() != null && !place.getPhotos().isEmpty()) {
                List<Photo> photos = place.getPhotos();

                //비율 1.45~1.85 범위 내 사진
                Photo selectedPhoto = photos.stream()
                        .filter(p -> {
                            double ratio = (double) p.getWidthPx() / p.getHeightPx();
                            return ratio >= MIN_HORIZONTAL_ASPECT_RATIO && ratio <= MAX_HORIZONTAL_ASPECT_RATIO;
                        })
                        .findFirst()
                        .orElse(null);

                // 없으면 가로가 더 긴 이미지 중 가장 비율 큰 사진
                if (selectedPhoto == null) {
                    selectedPhoto = photos.stream()
                            .filter(p -> p.getWidthPx() > p.getHeightPx())
                            .max(Comparator.comparingDouble(p -> (double) p.getWidthPx() / p.getHeightPx()))
                            .orElse(null);
                }

                //여전히 없으면 NULL 이미지 등록
                if (selectedPhoto == null) {
                    logger.info("No suitable photo found for '{}'", location.getName());
                    insertNullImage(location);
                    return;
                }

                //선택된 사진으로 Google Media API 호출
                String mediaUrl = String.format(
                        "https://places.googleapis.com/v1/%s/media?maxHeightPx=1600&skipHttpRedirect=true&key=%s",
                        selectedPhoto.getName(), googleApiKey
                );

                try {
                    HttpHeaders mediaHeaders = new HttpHeaders();
                    mediaHeaders.set("X-Goog-Api-Key", googleApiKey);
                    mediaHeaders.set("Accept", "application/json");
                    HttpEntity<Void> mediaRequest = new HttpEntity<>(mediaHeaders);

                    ResponseEntity<Map> mediaResponse =
                            restTemplate.exchange(mediaUrl, HttpMethod.GET, mediaRequest, Map.class);

                    if (mediaResponse.getStatusCode() == HttpStatus.OK && mediaResponse.getBody() != null) {
                        Object uriObj = mediaResponse.getBody().get("photoUri");
                        String photoUri = (uriObj != null) ? uriObj.toString() : null;

                        LocationImage image = new LocationImage();
                        image.setLocation(location);
                        image.setImageUrl(photoUri);
                        image.setCreatedAt(LocalDateTime.now());
                        locationImageRepository.save(image);

                        logger.info("Saved photo for '{}': {}", location.getName(),
                                (photoUri != null ? photoUri : "NULL"));
                    } else {
                        insertNullImage(location);
                    }
                } catch (RestClientException e) {
                    logger.warn("Photo API failed for '{}': {}", selectedPhoto.getName(), e.getMessage());
                    insertNullImage(location);
                }
            } else {
                insertNullImage(location);
            }

        } catch (RestClientException e) {
            logger.error("Google API call failed for '{}': {}", query, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error for '{}': {}", query, e.getMessage());
        }
    }

    private void insertNullImage(Location location){
        LocationImage nullImage = new LocationImage();
        nullImage.setLocation(location);
        nullImage.setImageUrl(null);
        nullImage.setCreatedAt(LocalDateTime.now());
        locationImageRepository.save(nullImage);
        logger.info("Inserted NULL image row for '{}'", location.getName());
    }

    // 500개 단위로 이미지 url 업데이트
    public void updateImagesForEmptyLocations(){
        List<Location> batch = locationRepository.findTop500ByGooglePlaceIdIsNull();
        logger.info("Updating images for '{}'", batch.size());

        for (Location loc : batch) {
            updateFromGoogleTextSearch(loc);
            try{
                Thread.sleep(500);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}