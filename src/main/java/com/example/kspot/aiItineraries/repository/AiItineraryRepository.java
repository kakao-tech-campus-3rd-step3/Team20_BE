package com.example.kspot.aiItineraries.repository;

import com.example.kspot.aiItineraries.entity.AiItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiItineraryRepository extends JpaRepository<AiItinerary, Long> {
    //user별 동선 조회
    List<AiItinerary> findByUser_UserId(Long userId);

    //저장된 ai 동선 최신순 정렬
    List<AiItinerary> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    //특정 일정 단건 조회
    Optional<AiItinerary> findByItineraryIdAndUser_UserId(Long itineraryId, Long userId);
}
