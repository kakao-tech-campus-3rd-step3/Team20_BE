package com.example.kspot.locationReview.dto;

public record CreateLocationReviewRequest(
    Long locationId,
    String title,
    String detail,
    Integer rating
){}
