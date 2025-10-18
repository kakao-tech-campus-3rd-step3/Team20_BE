package com.example.kspot.locationReview.dto;

import com.example.kspot.contents.dto.PaginationDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationReviewListDto {
  private List<LocationReviewDto> locationReviews;
  private PaginationDto pagination;
}
