package com.example.kspot.itineraries.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ItineraryLocationId {

  private Long itineraryId;
  private Long locationId;

  public ItineraryLocationId() {}

  public ItineraryLocationId(Long itineraryId, Long locationId) {
    this.itineraryId = itineraryId;
    this.locationId = locationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ItineraryLocationId)) return false;
    ItineraryLocationId that = (ItineraryLocationId) o;
    return Objects.equals(itineraryId, that.itineraryId) &&
        Objects.equals(locationId, that.locationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(itineraryId, locationId);
  }
}
