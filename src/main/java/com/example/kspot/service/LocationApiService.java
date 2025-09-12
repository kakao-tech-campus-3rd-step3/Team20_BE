package com.example.kspot.service;

import com.example.kspot.entity.Location;
import com.example.kspot.repository.LocationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class LocationApiService {
    private final String baseUrl;
    private final String serviceKey;
    private final LocationRepository locationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LocationApiService(
            @Value("${locationapi.base-url}") String baseUrl,
            @Value("${locationapi.service-key}") String serviceKey,
            LocationRepository locationRepository
    ){
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
        this.locationRepository = locationRepository;
    }

    public void fetchAndSaveLocations() throws Exception {
        String url = String.format("%s?page=3&perPage=20&serviceKey=%s", baseUrl, serviceKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode root = objectMapper.readTree(response.body());
            JsonNode items = root.path("data");

            for (JsonNode item : items) {
                String name = item.path("장소명").asText();
                String address = item.path("주소").asText();
                Double lat = parseDouble(item.path("위도").asText());
                Double lng = parseDouble(item.path("경도").asText());
                String desc = item.path("장소설명").asText();

                Location location = locationRepository.findByName(name)
                        .orElse(new Location());

                location.setName(name);
                location.setAddress(address);
                location.setLatitude(lat);
                location.setLongitude(lng);
                location.setDescription(desc);

                locationRepository.save(location);
            }
        }
    }

    private Double parseDouble(String val) {
        try { return Double.parseDouble(val); }
        catch (Exception e) { return null; }
    }
}
