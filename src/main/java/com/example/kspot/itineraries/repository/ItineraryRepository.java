package com.example.kspot.itineraries.repository;

import com.example.kspot.itineraries.entity.Itinerary;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {

}
