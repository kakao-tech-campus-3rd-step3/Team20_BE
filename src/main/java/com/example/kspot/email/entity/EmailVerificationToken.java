package com.example.kspot.email.entity;

import com.example.kspot.email.exception.ExpiredTokenException;
import com.example.kspot.email.exception.TokenAlreadyUsedException;
import com.example.kspot.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @Column(name = "token_hash_hex", nullable = false, length = 32)
    private String tokenHashHex;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private Integer sentCount = 1;

    @Column(nullable = false)
    private LocalDateTime lastSentAt = LocalDateTime.now();

    public void assertUsable(LocalDateTime now) {
        if (used) {
            throw new TokenAlreadyUsedException();
        }

        if (now.isAfter(expiresAt)) {
            throw new ExpiredTokenException(expiresAt);
        }
    }

}

