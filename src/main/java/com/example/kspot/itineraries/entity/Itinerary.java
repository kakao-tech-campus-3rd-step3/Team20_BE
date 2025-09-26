package com.example.kspot.itineraries.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "itineraries")
@Getter
@Setter
public class Itinerary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "itinerary_id")
  private long itineraryId;

  // 아직 User Entity가 만들어지지 않아서 userId로만 매핑
  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "created_at", updatable = false, insertable = false)
  private LocalDateTime created_at;

  @OneToMany(mappedBy = "itinerary", cascade = CascadeType.ALL)
  private List<ItineraryLocation> itineraryLocations = new ArrayList<>();

  // 동선에 장소 추가
  public void addItineraryLocation(ItineraryLocation itineraryLocation) {
    itineraryLocations.add(itineraryLocation);
    itineraryLocation.setItinerary(this);
  }

  // 동선에 장소 삭제
  public void removeItineraryLocation(ItineraryLocation itineraryLocation) {
    itineraryLocations.remove(itineraryLocation);
    itineraryLocation.setItinerary(null);
  }
}
