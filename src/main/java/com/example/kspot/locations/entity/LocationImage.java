package com.example.kspot.locations.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_images")
@Getter
@Setter
@NoArgsConstructor
public class LocationImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "img_url", columnDefinition = "TEXT")
    private String imageUrl;

    private LocalDateTime createdAt;
}
