package com.example.kspot.email.entity;

import com.example.kspot.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
public class EmailVerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "tokenHash", nullable = false, length = 32)
    private byte[] tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private Integer sentCount = 1;

    @Column(nullable = false)
    private LocalDateTime lastSentAt = LocalDateTime.now();

}

