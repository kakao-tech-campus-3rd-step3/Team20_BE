package com.example.kspot.email.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenProvider {

    private final SecureRandom random = new SecureRandom();
    private final MessageDigest sha256;

    public TokenProvider() throws NoSuchAlgorithmException {
        sha256 = MessageDigest.getInstance("SHA-256");
    }

    public String newRawToken() {
        byte[] buf = new byte[16]; // 128-bit
        random.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    public byte[] sha256(String raw) {
        return sha256.digest(raw.getBytes(StandardCharsets.UTF_8));
    }

}
