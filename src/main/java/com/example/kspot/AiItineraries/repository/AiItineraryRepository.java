package com.example.kspot.AiItineraries.repository;

import com.example.kspot.AiItineraries.entity.AiItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiItineraryRepository extends JpaRepository<AiItinerary, Long> {
    List<AiItinerary> findByUser_UserId(Long userId);
}
