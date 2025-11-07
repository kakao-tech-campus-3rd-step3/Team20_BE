package com.example.kspot.aiItineraries.service;

import com.example.kspot.aiItineraries.dto.AiItineraryListResponse;
import com.example.kspot.aiItineraries.dto.AiItineraryResponse;
import com.example.kspot.aiItineraries.entity.AiItinerary;
import com.example.kspot.aiItineraries.repository.AiItineraryRepository;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AiItineraryService {

    private final AiItineraryRepository itineraryRepository;
    private final UserRepository userRepository;

    //ai 생성 동선 저장
    public AiItineraryResponse save(Long userId, String startPoint, String endPoint,
                                    String duration, String theme, Map<String, Object> jsonData) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


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
