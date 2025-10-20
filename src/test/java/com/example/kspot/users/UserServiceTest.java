package com.example.kspot.users;

import com.example.kspot.config.SecurityConfig;
import com.example.kspot.email.service.EmailVerificationService;
import com.example.kspot.auth.jwt.JwtProvider;
import com.example.kspot.users.dto.UserRequestDto;
import com.example.kspot.users.dto.UserTokenResponseDto;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.exception.NotEmailVerifiedException;
import com.example.kspot.users.repository.UserRepository;
import com.example.kspot.users.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock JwtProvider jwtProvider;
    @Mock EmailVerificationService emailVerificationService;
    @Mock SecurityConfig securityConfig;

    @InjectMocks
    UserService userService;

    private UserRequestDto signupDto;
    private Users savedUser;

    @BeforeEach
    void setUp() {

        signupDto = new UserRequestDto("yuuu5683@naver.com", "joongsun", "1234");
        savedUser = new Users(
                1L,
                "yuuu5683@naver.com",
                "joongsun",
                "LOCAL",
                "{bcrypt}encoded",
                null,
                false,
                null,
                null
        );
    }

    @Test
    @DisplayName("성공: 비번 인코딩 + 사용자 저장 + 인증메일 발송")
    void register_success() {

        // given
        when(securityConfig.encodePassword("1234")).thenReturn("{bcrypt}encoded");
        when(userRepository.save(any(Users.class))).thenReturn(savedUser);

        // when
        userService.register(signupDto);

        // then
        ArgumentCaptor<Users> captor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository, times(1)).save(captor.capture());
        Users toSave = captor.getValue();

        assertThat(toSave.getEmail()).isEqualTo("yuuu5683@naver.com");
        assertThat(toSave.getNickname()).isEqualTo("joongsun");
        assertThat(toSave.getPassword()).isEqualTo("{bcrypt}encoded");
        assertThat(toSave.isEmailVerified()).isFalse();

        // 인증 메일 발송 호출 확인
        verify(emailVerificationService, times(1)).send("yuuu5683@naver.com",0);
    }

    @Nested
    @DisplayName("다양한 로그인 성공 실패 테스트")
    class LoginTests {

        @Test
        @DisplayName("로그인 성공")
        void login_success() {
            // given
            // 로그인 성공을 위한 이메일 인증 성공
            savedUser.setEmailVerified(true);

            when(userRepository.findUsersByEmail("yuuu5683@naver.com"))
                    .thenReturn(Optional.of(savedUser));
            when(securityConfig.matches("1234", "{bcrypt}encoded")).thenReturn(true);
            when(jwtProvider.generateAccessToken(savedUser)).thenReturn("ACCESS.JWT.TOKEN");
            when(jwtProvider.generateRefreshToken(savedUser)).thenReturn("REFRESH.JWT.TOKEN");

            // when
            UserTokenResponseDto dto = userService.login(signupDto);

            // then
            assertThat(dto).isNotNull();
            assertThat(dto.accessToken()).isEqualTo("ACCESS.JWT.TOKEN");
            assertThat(dto.refreshToken()).isEqualTo("REFRESH.JWT.TOKEN");
        }

        @Test
        @DisplayName("이메일 없음 -> BadCredentialsException")
        void login_email_not_found() {
            //given
            when(userRepository.findUsersByEmail("yuuu5683@naver.com"))
                    .thenReturn(Optional.empty());

            //when & then
            assertThatThrownBy(() -> userService.login(signupDto))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("이메일이 일치하지 않습니다");
        }

        @Test
        @DisplayName("비밀번호 불일치 -> BadCredentialsException")
        void login_wrong_password() {

            //given
            when(userRepository.findUsersByEmail("yuuu5683@naver.com"))
                    .thenReturn(Optional.of(savedUser));
            when(securityConfig.matches("1234", "{bcrypt}encoded")).thenReturn(false);

            //when & then
            assertThatThrownBy(() -> userService.login(signupDto))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("비밀번호가 일치하지 않습니다");
        }

        @Test
        @DisplayName("이메일 미인증 -> NotEmailVerifiedException")
        void login_not_email_verified() {

            //given
            when(userRepository.findUsersByEmail("yuuu5683@naver.com"))
                    .thenReturn(Optional.of(savedUser));
            when(securityConfig.matches("1234", "{bcrypt}encoded")).thenReturn(true);

            //when & then
            assertThatThrownBy(() -> userService.login(signupDto))
                    .isInstanceOf(NotEmailVerifiedException.class)
                    .hasMessageContaining("이메일 인증이 되지 않은 계정입니다");
        }
    }

}
