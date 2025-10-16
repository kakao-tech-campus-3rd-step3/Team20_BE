package com.example.kspot.email.controller;


import com.example.kspot.email.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("token") String token) {
        verificationService.verifyByRawToken(token);
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String,String> body) {
        String email = body.getOrDefault("email", "");
        verificationService.send(email);
        return ResponseEntity.ok(Map.of("message", "인증 메일을 확인해 주세요(이미 인증된 경우 무시)."));
    }
}

