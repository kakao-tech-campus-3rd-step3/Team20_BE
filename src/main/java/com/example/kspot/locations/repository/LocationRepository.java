package com.example.kspot.locations.repository;

import com.example.kspot.locations.entity.Location;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    List<Location> findTop5ByGooglePlaceIdIsNull();

    @Query("""
        SELECT l FROM Location l
        WHERE l.latitude BETWEEN :minLat AND :maxLat
          AND l.longitude BETWEEN :minLon AND :maxLon
    """)
    List<Location> findByLatitudeAndLongitudeRange(
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLon") Double minLon,
        @Param("maxLon") Double maxLon
    );
}