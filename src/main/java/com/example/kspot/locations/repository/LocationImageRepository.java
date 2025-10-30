package com.example.kspot.locations.repository;

import com.example.kspot.locations.entity.LocationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationImageRepository extends JpaRepository<LocationImage, Long> {
    List<LocationImage> findByLocation_LocationId(Long locationId);
}
