package com.example.kspot.contents.entity;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ContentArtistId {
  private Long contentId;
  private Long artistId;

  public ContentArtistId(){}
  public ContentArtistId(Long contentId, Long artistId) {
    this.contentId = contentId;
    this.artistId = artistId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ContentLocationId)) {
      return false;
    }
    ContentArtistId that = (ContentArtistId) o;
    return Objects.equals(contentId, that.contentId) && Objects.equals(artistId, that.artistId);
  }


  public Long getContentId() { return contentId; }
  public Long getArtistId() { return artistId; }
}
