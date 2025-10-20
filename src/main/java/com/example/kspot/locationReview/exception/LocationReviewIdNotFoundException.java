package com.example.kspot.locationReview.exception;

public class LocationReviewIdNotFoundException extends RuntimeException {

  public LocationReviewIdNotFoundException(Long reviewId) {
    super("해당 Location 리뷰를 찾을 수 없습니다. reviewId = " + reviewId);
  }
}
