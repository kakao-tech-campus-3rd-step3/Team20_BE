package com.example.kspot.itineraries.repository;

import com.example.kspot.itineraries.entity.Itinerary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

}
