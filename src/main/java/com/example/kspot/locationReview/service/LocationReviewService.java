package com.example.kspot.locationReview.service;

import com.example.kspot.locationReview.entity.LocationReview;
import com.example.kspot.locationReview.exception.LocationReviewNotFoundException;
import com.example.kspot.locationReview.repository.LocationReviewRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LocationReviewService {

  private final LocationReviewRepository locationReviewRepository;

  @Autowired
  public LocationReviewService(LocationReviewRepository locationReviewRepository) {
    this.locationReviewRepository = locationReviewRepository;
  }

  // 리뷰 생성
  public LocationReview createReview(LocationReview review) {
    return locationReviewRepository.save(review);
  }

  // ReviewId를 통한 리뷰 조회
  public Optional<LocationReview> findByLocationReviewId(Long locationReviewId) {
    return locationReviewRepository.findByReviewId(locationReviewId);
  }

  // LocationId를 통한 장소에 대한 리뷰목록 조회
  public Page<LocationReview> findByLocationId(Long locationId, Pageable pageable) {
    return locationReviewRepository.findByLocationId(locationId, pageable);
  }

  // userID + ReviewId를 통한 리뷰 수정
  public LocationReview updateReview(Long userId, Long reviewId, LocationReview updateReview) {
    LocationReview existingReview = locationReviewRepository.findByReviewId(reviewId)
        .orElseThrow(() -> new LocationReviewNotFoundException("리뷰를 찾을 수 없습니다"));

    if (!existingReview.getUserId().equals(userId)) {
      throw new SecurityException("본인의 리뷰만 수정할 수 있습니다");
    }

    existingReview.setTitle(updateReview.getTitle());
    existingReview.setDetail(updateReview.getDetail());
    existingReview.setRating(updateReview.getRating());
    existingReview.setUpdatedAt(LocalDateTime.now());

    return locationReviewRepository.save(existingReview);
  }

  // userID + ReviewId를 통한 리뷰 삭제
  public void deleteReview(Long userId, Long reviewId) {
    LocationReview existingReview = locationReviewRepository.findByReviewId(reviewId)
        .orElseThrow(() -> new LocationReviewNotFoundException("리뷰를 찾을 수 없습니다"));

    if (!existingReview.getUserId().equals(userId)) {
      throw new SecurityException("본인의 리뷰만 삭제할 수 있습니다");
    }

    locationReviewRepository.delete(existingReview);
  }
}
