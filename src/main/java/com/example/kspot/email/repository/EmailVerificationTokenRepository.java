package com.example.kspot.email.repository;

import com.example.kspot.email.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    @Modifying
    @Query("update EmailVerificationToken t set t.used = true where t.user.userId = :userId and t.used = false")
    int invalidateAllActiveByUserId(@Param("userId") Long userId);

    Optional<EmailVerificationToken> findByTokenHashHex(String tokenHashHex);

}
