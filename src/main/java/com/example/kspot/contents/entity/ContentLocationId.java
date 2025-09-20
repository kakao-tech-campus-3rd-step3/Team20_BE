package com.example.kspot.contents.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ContentLocationId implements Serializable {

  private Long contentId;
  private Long locationId;

  public ContentLocationId() {
  }

  public ContentLocationId(Long contentId, Long locationId) {
    this.contentId = contentId;
    this.locationId = locationId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ContentLocationId)) {
      return false;
    }
    ContentLocationId that = (ContentLocationId) o;
    return Objects.equals(contentId, that.contentId) && Objects.equals(locationId, that.locationId);
  }

  public Long getContentId() { return contentId; }
  public Long getLocationId() { return locationId; }
}
