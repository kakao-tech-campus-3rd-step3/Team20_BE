package com.example.kspot.users.service;

import com.example.kspot.config.SecurityConfig;
import com.example.kspot.email.service.EmailVerificationService;
import com.example.kspot.auth.jwt.JwtProvider;
import com.example.kspot.users.dto.*;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.exception.DuplicateNicknameException;
import com.example.kspot.users.exception.DuplicateRegisterException;
import com.example.kspot.users.exception.NotEmailVerifiedException;
import com.example.kspot.users.exception.NotFoundUserException;
import com.example.kspot.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final EmailVerificationService emailVerificationService;
    private final SecurityConfig securityConfig;

    public List<UserInfoResponseDto> getUsers(){
        return userRepository.findAll().stream().map(UserInfoResponseDto::fromEntity).toList();
    }

    public UserInfoResponseDto getUserById(Long id) {
        return userRepository.findById(id).map(UserInfoResponseDto::fromEntity).orElse(null);

        //Todo 사용브랜치에 전역예외처리 클래스가 없어 추후 예외처리할 예정
    }

    @Transactional
    public void register(UserRequestDto user) {

        //이미 db에 이메일이 있을 경우 해당 이메일로 인증 전송
        Optional<Users> opt = userRepository.findUsersByEmail(user.email());

        if(opt.isPresent() && opt.get().isEmailVerified()){
            throw new DuplicateRegisterException();
        }

        if (opt.isPresent()) {
            Users existing = opt.get();
            existing.setPassword(securityConfig.encodePassword(user.password()));
            userRepository.save(existing);
            emailVerificationService.send(existing.getEmail(),0);
            return;
        }

        if(userRepository.findUsersByNickname(user.nickname()).isPresent()){
            throw new DuplicateNicknameException();
        }

        String encodedPw = securityConfig.encodePassword(user.password());

        Users users = new Users(
                null,
                user.email(),
                user.nickname(),
                null,
                encodedPw,
                null,
                false,
                null,
                null
        );
        userRepository.save(users);

        emailVerificationService.send(user.email(),0);
    }

    @Transactional
    public UserUpdateResponseDto updateUser(Long id, UserUpdataRequestDto dto) {
        Users user = userRepository.findById(id).orElse(null);
        user.setNickname(dto.nickname());
        userRepository.save(user);
        return new UserUpdateResponseDto(dto.nickname());
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserTokenResponseDto login(UserRequestDto userRequestDto){

        Users user = userRepository.findUsersByEmail(userRequestDto.email()).orElseThrow(
                () -> new BadCredentialsException("이메일이 일치하지 않습니다")
        );

        if(!securityConfig.matches(userRequestDto.password(), user.getPassword())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }

        if(!user.isEmailVerified()){
            throw new NotEmailVerifiedException("이메일 인증이 되지 않은 계정입니다");
        }

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new UserTokenResponseDto(accessToken , refreshToken);
    }

    @Transactional
    public UserTokenResponseDto logout(Long userId) {
        Users user = userRepository.findById(userId).orElse(null);
        user.setRefreshToken(null);
        userRepository.save(user);

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return new UserTokenResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public void resetPassword(Long id , UserResetPwDto dto) {

        Optional<Users> user = userRepository.findById(id);

        if(!user.isPresent()){
            throw new BadCredentialsException("이메일이 존재하지 않습니다");
        }

        String encodedPw = securityConfig.encodePassword(dto.password());
        user.get().setPassword(encodedPw);
        user.get().setEmailVerified(true);
        userRepository.save(user.get());

    }

    public UserStatusResponseDto getStatus(String refreshToken) {

        Long userId = jwtProvider.validateToken(refreshToken);
        return new UserStatusResponseDto( userId != null, userId );
    }

    public String getRefreshToken(String refreshToken) {
        Long userId = jwtProvider.validateToken(refreshToken);
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException());

        return jwtProvider.generateAccessToken(user);

    }

}
