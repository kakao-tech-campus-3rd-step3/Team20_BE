package com.example.kspot.locationReview.exception;

public class LocationReviewNotFoundException extends RuntimeException {

  public LocationReviewNotFoundException(Long locationId) {
    super("해당 Location의 리뷰가 존재하지 않습니다 : " + locationId);
  }
}
