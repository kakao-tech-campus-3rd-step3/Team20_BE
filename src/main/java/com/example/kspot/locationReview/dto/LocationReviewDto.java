package com.example.kspot.locationReview.dto;

import com.example.kspot.locationReview.entity.LocationReview;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationReviewDto {
  private Long reviewId;
  private Long locationId;
  private Long userId;
  private String title;
  private Integer rating;

  public static LocationReviewDto fromEntity(LocationReview entity){
    return new LocationReviewDto(
        entity.getReviewId(),
        entity.getLocationId(),
        entity.getUserId(),
        entity.getTitle(),
        entity.getRating());
  }
}
