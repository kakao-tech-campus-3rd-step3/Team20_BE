package com.example.kspot.itineraries.entity;

import com.example.kspot.locations.entity.Location;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "itinerary_location")
@Getter
@Setter
public class ItineraryLocation {
  @EmbeddedId
  private ItineraryLocationId id = new ItineraryLocationId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("itineraryId") // 복합키의 itineraryId 사용
  @JoinColumn(name = "itinerary_id", nullable = false)
  private Itinerary itinerary;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("locationId") // 복합키의 locationId 사용
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @Column(name = "visit_order", nullable = false)
  private int visitOrder;
}
