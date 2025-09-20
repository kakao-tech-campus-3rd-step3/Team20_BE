package com.example.kspot.contents.entity;

import com.example.kspot.locations.entity.Location;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name = "content_location")
public class ContentLocation {

  @EmbeddedId
  private ContentLocationId id;

  @ManyToOne
  @MapsId("contentId")
  @JoinColumn(name = "content_id")
  private Content content;

  @ManyToOne
  @MapsId("locationId")
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "scene_description")
  private String sceneDescription;

  // Getter & Setter
  public ContentLocationId getId() { return id; }
  public void setId(ContentLocationId id) { this.id = id; }

  public Content getContent() { return content; }
  public Location getLocation() { return location; }
  public String getSceneDescription() { return sceneDescription; }
}

