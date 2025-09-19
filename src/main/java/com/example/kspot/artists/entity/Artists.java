package com.example.kspot.artists.entity;

import com.example.kspot.contents.entity.Content;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "artists")
public class Artists {

  @Id
  @Column(name = "artist_id")
  private Long artistId;

  @Column(nullable = false)
  private String name;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Long getArtistId() {
    return artistId;
  }

  public String getName() {
    return name;
  }

  public void setArtistId(Long artistId) {
    this.artistId = artistId;
  }

  public void setName(String name) {
    this.name = name;
  }

  // many to many 역방향 매핑
  @ManyToMany(mappedBy = "artists")
  private Set<Content> contents = new HashSet<>();

  public Set<Content> getContents() {
    return contents;
  }
}
