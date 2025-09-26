package com.example.kspot.contents.entity;

import com.example.kspot.artists.entity.Artists;
import com.example.kspot.locations.entity.Location;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "contents")
public class Content {

  // TMDB에서 받아오는 정보(id는 검색 편의성을 위해 TMDB의 id를 그대로 사용)
  @Id
  @Column(name = "content_id")
  private long content_id;

  @Column(name = "category")
  private String category;

  @Column(name = "title")
  private String title;

  @Column(name = "poster_image_url")
  private String poster_image_url;

  @Column(name = "release_date")
  private LocalDateTime release_date;


  // KSPOT 서비스에서 생성되는 정보(생성날짜, 업데이트된 날짜)
  @CreationTimestamp
  @Column(updatable = false, name = "created_at")
  private LocalDateTime created_at;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updated_at;

  // Content_Artist와 one to many 관계 맺도록 설정
  @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ContentArtist> contentArtists = new ArrayList<>();

  // Cotent_Location과 one to many 관계 맺도록 설정
  @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ContentLocation> contentLocations = new ArrayList<>();


  // Getter
  public long getContent_id() {return content_id;}
  public String getCategory() {return category;}
  public String getTitle() {return title;}
  public String getPoster_image_url() {return poster_image_url;}
  public LocalDateTime getRelease_date() {return release_date;}
  public LocalDateTime getCreated_at() {return created_at;}
  public LocalDateTime getUpdated_at() {return updated_at;}

  public List<ContentArtist> getContentArtists() { return contentArtists; }
  public List<ContentLocation> getContentLocations() { return contentLocations; }
  // Setter는 현재 MVP에서는 필요없을 것이라 생각해 생략
  // 이후, 관리자 페이지 생성시 필요해질 것으로 예상해 차후 추가예정
}
