package com.example.kspot.locationReview.dto;

import java.util.List;

public record CreateLocationReviewRequest(
    Long locationId,
    String title,
    String detail,
    Integer rating
){}
