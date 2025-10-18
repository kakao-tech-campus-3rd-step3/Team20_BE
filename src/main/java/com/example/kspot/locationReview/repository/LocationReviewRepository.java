package com.example.kspot.locationReview.repository;

import com.example.kspot.locationReview.entity.LocationReview;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationReviewRepository extends JpaRepository<LocationReview, Long> {
  Page<LocationReview> findByLocationId(Long locationId, Pageable pageable);

  Optional<LocationReview> findByReviewId(Long reviewId);
}
