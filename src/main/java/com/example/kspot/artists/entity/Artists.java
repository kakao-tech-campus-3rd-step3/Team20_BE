package com.example.kspot.artists.entity;

import jakarta.persistence.*;
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

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
