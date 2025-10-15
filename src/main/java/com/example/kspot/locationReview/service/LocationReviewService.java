package com.example.kspot.locationReview.service;

import com.example.kspot.locationReview.dto.CreateLocationReviewRequest;
import com.example.kspot.locationReview.entity.LocationReview;
import com.example.kspot.locationReview.exception.LocationReviewNotFoundException;
import com.example.kspot.locationReview.repository.LocationReviewRepository;
import com.example.kspot.locations.exception.LocationNotFoundException;
import com.example.kspot.locations.repository.LocationRepository;
import com.example.kspot.users.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LocationReviewService {

  private final LocationReviewRepository locationReviewRepository;
  private final UserRepository userRepository;
  private final LocationRepository locationRepository;

  @Autowired
  public LocationReviewService(LocationReviewRepository locationReviewRepository,
      UserRepository userRepository, LocationRepository locationRepository) {
    this.locationReviewRepository = locationReviewRepository;
    this.userRepository = userRepository;
    this.locationRepository = locationRepository;
  }

  // 리뷰 생성
  public LocationReview createReview(CreateLocationReviewRequest request, Long userId) {

    userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다"));

    locationRepository.findById(request.locationId())
        .orElseThrow(() -> new LocationNotFoundException(request.locationId()));

    LocationReview lR = new LocationReview();
    lR.setLocationId(request.locationId());
    lR.setUserId(userId);
    lR.setTitle(request.title());
    lR.setDetail(request.detail());
    lR.setRating(request.rating());
    lR.setCreatedAt(LocalDateTime.now());
    lR.setUpdatedAt(LocalDateTime.now());

    return locationReviewRepository.save(lR);
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
