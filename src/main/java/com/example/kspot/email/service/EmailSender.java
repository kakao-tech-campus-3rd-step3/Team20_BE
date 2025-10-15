package com.example.kspot.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;
    @Value("${app.frontendOrigin}")
    private String frontendOrigin;
    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendVerificationMail(String toEmail, String rawToken) {
        String link = frontendOrigin + "/verify-email?token=" + rawToken;

        String subject = "[K-SPOT] 이메일 인증을 완료해 주세요";
        String body = """
                안녕하세요,
                아래 링크를 눌러 이메일 인증을 완료해 주세요.
                %s

                만약 본인이 요청하지 않았다면 이 메일을 무시하셔도 됩니다.
                """.formatted(link);

        var msg = new SimpleMailMessage();
        msg.setFrom(mailUsername);
        msg.setTo(toEmail);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
