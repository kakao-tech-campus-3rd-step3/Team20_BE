package com.example.kspot.itineraries.repository;

import com.example.kspot.itineraries.entity.Itinerary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

  List<Itinerary> findByUser_UserId(Long userId);
}
