package com.example.kspot.itineraries.service;

import com.example.kspot.itineraries.dto.CreateItineraryRequest;
import com.example.kspot.itineraries.dto.ItineraryLocationResponse;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.dto.UserSummaryResponse;
import com.example.kspot.itineraries.entity.Itinerary;
import com.example.kspot.itineraries.entity.ItineraryLocation;
import com.example.kspot.itineraries.exception.ItineraryNotFoundException;
import com.example.kspot.itineraries.exception.LocationNotFoundException;
import com.example.kspot.itineraries.repository.ItineraryLocationRepository;
import com.example.kspot.itineraries.repository.ItineraryRepository;
import com.example.kspot.locations.entity.Location;
import com.example.kspot.locations.repository.LocationRepository;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ItineraryService {

  @PersistenceContext
  private EntityManager entityManager;

  private final ItineraryRepository itineraryRepository;
  private final LocationRepository locationRepository;
  private final UserRepository userRepository;
  private final ItineraryLocationRepository itineraryLocationRepository;

  @Autowired
  public ItineraryService(ItineraryRepository itineraryRepository,
      LocationRepository locationRepository,
      UserRepository userRepository,
      ItineraryLocationRepository itineraryLocationRepository) {
    this.itineraryRepository = itineraryRepository;
    this.locationRepository = locationRepository;
    this.userRepository = userRepository;
    this.itineraryLocationRepository = itineraryLocationRepository;
  }

  public ItineraryResponseDto getItineraryById(Long id) {
    return itineraryRepository.findById(id)
        .map(ItineraryResponseDto::fromEntity)
        .orElseThrow(() -> new ItineraryNotFoundException("존재하지 않는 여행계획 입니다"));
  }

  public List<ItineraryResponseDto> getItinerariesByUserId(Long userId) {
    List<Itinerary> itineraryList = itineraryRepository.findByUser_UserId(userId);

    return itineraryList.stream()
        .map(ItineraryResponseDto::fromEntity)
        .toList();
  }



  public ItineraryResponseDto createItinerary(CreateItineraryRequest request, Long userId) {
    Users user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다"));

    Itinerary itinerary = new Itinerary();
    itinerary.setUser(user);
    itinerary.setTitle(request.title());
    itinerary.setDescription(request.description());
    itinerary.setCreated_at(LocalDateTime.now());

    List<ItineraryLocation> itineraryLocations = new ArrayList<>();
    for (CreateItineraryRequest.LocationRequest locReq : request.locations()) {
      Location loc = locationRepository.findById(locReq.locationId())
          .orElseThrow(() -> new LocationNotFoundException("잘못된 Location Id입니다"));

      ItineraryLocation il = new ItineraryLocation();
      il.setItinerary(itinerary);
      il.setLocation(loc);
      il.setVisitOrder(locReq.visitOrder());
      itineraryLocations.add(il);
    }
    itinerary.setItineraryLocations(itineraryLocations);

    Itinerary saved = itineraryRepository.save(itinerary);

    return new ItineraryResponseDto(
        saved.getItineraryId(),
        new UserSummaryResponse(userId, null),
        saved.getTitle(),
        saved.getDescription(),
        saved.getCreated_at(),
        saved.getItineraryLocations().stream()
            .map(il -> new ItineraryLocationResponse(
                il.getLocation().getLocationId(),
                il.getLocation().getName(),
                il.getLocation().getAddress(),
                il.getVisitOrder()
            )).toList()
    );
  }

  @Transactional
  public ItineraryResponseDto updateItinerary(Long itineraryId, CreateItineraryRequest request, Long userId) {
    Itinerary itinerary = itineraryRepository.findById(itineraryId)
        .orElseThrow(() -> new ItineraryNotFoundException("존재하지 않는 여행계획 입니다"));

    if (!itinerary.getUser().getUserId().equals(userId)) {
      throw new RuntimeException("해당 계획에 대한 수정 권한이 없습니다");
    }

    // 기존 Locations DB에서 직접 삭제
    itineraryLocationRepository.deleteByItineraryId(itineraryId);

    itinerary.setTitle(request.title());
    itinerary.setDescription(request.description());

    // 새 Locations 추가
    for (CreateItineraryRequest.LocationRequest locReq : request.locations()) {
      Location loc = locationRepository.findById(locReq.locationId())
          .orElseThrow(() -> new LocationNotFoundException("잘못된 Location Id입니다."));
      ItineraryLocation il = new ItineraryLocation();
      il.setItinerary(itinerary);
      il.setLocation(loc);
      il.setVisitOrder(locReq.visitOrder());
      itinerary.addItineraryLocation(il);
    }

    Itinerary saved = itineraryRepository.save(itinerary);

    return new ItineraryResponseDto(
        saved.getItineraryId(),
        new UserSummaryResponse(userId, "Test Nick Name"),
        saved.getTitle(),
        saved.getDescription(),
        saved.getCreated_at(),
        saved.getItineraryLocations().stream()
            .map(il -> new ItineraryLocationResponse(
                il.getLocation().getLocationId(),
                il.getLocation().getName(),
                il.getLocation().getAddress(),
                il.getVisitOrder()
            )).toList()
    );
  }



  public void deleteItinerary(Long itineraryId, Long userId) {
    Itinerary itinerary = itineraryRepository.findById(itineraryId)
        .orElseThrow(() -> new ItineraryNotFoundException("존재하지 않는 여행 계획입니다"));

    if(!itinerary.getUser().getUserId().equals(userId)) {
      throw new RuntimeException("해당 계획에 대한 삭제 권한이 없습니다");
    }
    itineraryRepository.delete(itinerary);
  }
}
