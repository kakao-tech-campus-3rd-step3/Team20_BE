package com.example.kspot.Security;

import com.example.kspot.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHashingTest {

    private SecurityConfig config;
    private PasswordEncoder passwordEncoder;
    private String pepper;
    private String raw;

    @BeforeEach
    void setUp() {
        config = new SecurityConfig();
        pepper = "testPepper!";
        raw = "1234";
    }

    @Test
    @DisplayName("같은 비밀번호라도 해시가 매번 달라지고 match는 true")
    void bcrypt_random_salt_and_matches_true() {
        // given
        ReflectionTestUtils.setField(config, "pepper", pepper);

        String hash1 = config.encodePassword(raw);
        String hash2 = config.encodePassword(raw);

        // then
        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(config.matches(raw, hash1)).isTrue();
        assertThat(config.matches(raw, hash2)).isTrue();
    }

    @Test
    @DisplayName("비밀번호가 다르면 false")
    void bcrypt_mismatch_password() {
        // given
        ReflectionTestUtils.setField(config, "pepper", pepper);

        String correct = "correct-password";
        String wrong = "wrong-password";
        String storedHash = config.encodePassword(correct);

        // then
        assertThat(config.matches(wrong, storedHash)).isFalse();
        assertThat(config.matches(correct, storedHash)).isTrue();
    }

    @Test
    @DisplayName("같은 비밀번호라도 pepper가 다르면 match는 false")
    void bcrypt_mismatch_pepper() {
        // given
        SecurityConfig configA = new SecurityConfig();
        SecurityConfig configB = new SecurityConfig();
        ReflectionTestUtils.setField(configA, "pepper", "PEPPER_A");
        ReflectionTestUtils.setField(configB, "pepper", "PEPPER_B");

        //when
        String storedHash = configA.encodePassword(raw);

        // then
        assertThat(configB.matches(raw, storedHash)).isFalse();
        assertThat(configA.matches(raw, storedHash)).isTrue();
    }
}

