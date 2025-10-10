package com.example.kspot.itineraries.repository;

import com.example.kspot.itineraries.entity.ItineraryLocation;
import com.example.kspot.itineraries.entity.ItineraryLocationId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryLocationRepository extends
    JpaRepository<ItineraryLocation, ItineraryLocationId> {

  /**
   * 특정 itineraryId에 속한 모든 ItineraryLocation 삭제
   */
  @Modifying
  @Transactional
  @Query("DELETE FROM ItineraryLocation il WHERE il.itinerary.itineraryId = :itineraryId")
  void deleteByItineraryId(@Param("itineraryId") Long itineraryId);
}
