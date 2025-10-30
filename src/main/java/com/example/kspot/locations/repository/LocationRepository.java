package com.example.kspot.locations.repository;

import com.example.kspot.locations.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    List<Location> findTop5ByGooglePlaceIdIsNull();
}