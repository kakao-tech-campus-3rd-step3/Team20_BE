package com.example.kspot.email.service;

import com.example.kspot.email.dto.EmailResponseDto;
import com.example.kspot.email.entity.EmailVerificationToken;
import com.example.kspot.email.exception.TokenNotFoundException;
import com.example.kspot.email.repository.EmailVerificationTokenRepository;
import com.example.kspot.jwt.JwtProvider;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailSender emailSender;
    private final TokenProvider tokenProvider;
    private final JwtProvider jwtProvider;

    @Transactional
    public void issueAndSend(Long userId , int caseCode) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        if (user.isEmailVerified()) return;

        tokenRepository.invalidateAllActiveByUserId(userId);

        String raw = tokenProvider.newRawToken();
        byte[] hash = tokenProvider.sha256(raw);
        String hex = HexFormat.of().formatHex(hash).toLowerCase();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHashHex(hex);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(token);

        switch (caseCode) {
            case 0 -> emailSender.sendVerificationMail(user.getEmail(), raw);
            case 1 -> emailSender.sendResetPasswordMail(user.getEmail(), raw);
            default -> {}
        }

    }

    @Transactional
    public EmailResponseDto verifyByRawToken(String rawToken) {
        byte[] hash = tokenProvider.sha256(rawToken);
        String hex = HexFormat.of().formatHex(hash).toLowerCase();
        EmailVerificationToken t = tokenRepository.findByTokenHashHex(hex)
                .orElseThrow(() -> new TokenNotFoundException(hex));

        //시간 만료 검증
        LocalDateTime now = LocalDateTime.now();
        t.assertUsable(now);

        Users user = t.getUser();

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        t.setUsed(true);
        tokenRepository.save(t);

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new EmailResponseDto(accessToken, refreshToken);

    }

    @Transactional
    public void send(String email , int caseCode) {
        Users user = userRepository.findUsersByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾지 못했습니다.")
        );
        if (user == null || user.isEmailVerified()) return;
        issueAndSend(user.getUserId() , caseCode);
    }
}

