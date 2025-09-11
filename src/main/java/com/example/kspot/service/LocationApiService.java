package com.example.kspot.service;

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

    public LocationApiService(
            @Value("${locationapi.base-url}") String baseUrl,
            @Value("${locationapi.service-key}") String serviceKey
    ){
        this.baseUrl = baseUrl;
        this.serviceKey = serviceKey;
    }

    public void callApi() throws Exception{
        String url = String.format("%s?page=1&perPage=10&serviceKey=%s",
                baseUrl, serviceKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }
}
