package com.example.kspot.contents.entity;

import com.example.kspot.artists.entity.Artists;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "content_artist")
public class ContentArtist {

  @EmbeddedId
  private ContentArtistId id;

  @ManyToOne
  @MapsId("contentId")
  @JoinColumn(name = "content_id")
  private Content content;

  @ManyToOne
  @MapsId("artistId")
  @JoinColumn(name = "artist_id")
  private Artists artists;

  public ContentArtistId getId() {return id;}
  public void setId(ContentArtistId id) {this.id = id;}

  public Content getContent() {return content;}
  public Artists getArtists() {return artists;}
}
