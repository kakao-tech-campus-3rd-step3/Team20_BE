package com.example.kspot.email.service;

import com.example.kspot.email.entity.EmailVerificationToken;
import com.example.kspot.email.exception.TokenNotFoundException;
import com.example.kspot.email.repository.EmailVerificationTokenRepository;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailSender emailSender;
    private final TokenProvider tokenProvider;

    @Transactional
    public void issueAndSend(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        if (user.isEmailVerified()) return;


        tokenRepository.invalidateAllActiveByUserId(userId);

        String raw = tokenProvider.newRawToken();
        byte[] hash = tokenProvider.sha256(raw);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(hash);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(token);

        emailSender.sendVerificationMail(user.getEmail(), raw);
    }

    @Transactional
    public void verifyByRawToken(String rawToken) {
        byte[] hash = tokenProvider.sha256(rawToken);
        EmailVerificationToken t = tokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new TokenNotFoundException(hash));

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
    }

    @Transactional
    public void resend(String email) {
        Users user = userRepository.findUsersByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾지 못했습니다.")
        );
        if (user == null || user.isEmailVerified()) return;
        issueAndSend(user.getUserId());
    }
}

