package com.example.kspot.AiItineraries.entity;

import com.example.kspot.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_itineraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiItinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itineraryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, length = 100)
    private String startPoint;

    @Column(nullable = false, length = 100)
    private String endPoint;

    @Column(nullable = false, length = 50)
    private String duration;

    @Column(length = 50)
    private String theme = "all";

    @Column(columnDefinition = "JSON", nullable = false)
    private String data;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
