package com.example.kspot.AiItineraries.service;

import com.example.kspot.AiItineraries.dto.AiItineraryListResponse;
import com.example.kspot.AiItineraries.dto.AiItineraryResponse;
import com.example.kspot.AiItineraries.entity.AiItinerary;
import com.example.kspot.AiItineraries.repository.AiItineraryRepository;
import com.example.kspot.users.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiItineraryService {

    private final AiItineraryRepository itineraryRepository;

    //ai 생성 동선 저장
    public AiItineraryResponse save(Users user, String startPoint, String endPoint, String duration, String theme, String jsonData) {
        AiItinerary itinerary = AiItinerary.builder()
                .user(user)
                .startPoint(startPoint)
                .endPoint(endPoint)
                .duration(duration)
                .theme(theme)
                .data(jsonData)
                .build();

        return AiItineraryResponse.fromEntity(itineraryRepository.save(itinerary));
    }

    //일정 전체 조회
    @Transactional(readOnly = true)
    public List<AiItineraryListResponse> getUserItineraries(Long userId, boolean sorted) {
        List<AiItinerary> list = sorted
                ? itineraryRepository.findByUser_UserIdOrderByCreatedAtDesc(userId)
                : itineraryRepository.findByUser_UserId(userId);

        return list.stream()
                .map(AiItineraryListResponse::fromEntity)
                .toList();
    }

    //단건 조회
    @Transactional(readOnly = true)
    public AiItineraryResponse getUserItinerary(Long userId, Long itineraryId) {
        AiItinerary itinerary = itineraryRepository.findByItineraryIdAndUser_UserId(itineraryId, userId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        return AiItineraryResponse.fromEntity(itinerary);
    }

    //일정 삭제
    public void deleteItinerary(Long itineraryId) {
        itineraryRepository.deleteById(itineraryId);
    }
}
