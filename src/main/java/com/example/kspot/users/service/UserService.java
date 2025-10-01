package com.example.kspot.users.service;

import com.example.kspot.jwt.JwtProvider;
import com.example.kspot.users.dto.*;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public UserService(UserRepository userRepository , JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    public List<UserInfoResponseDto> getUsers(){
        return userRepository.findAll().stream().map(UserInfoResponseDto::fromEntity).toList();
    }

    public UserInfoResponseDto getUserById(Long id) {
        return userRepository.findById(id).map(UserInfoResponseDto::fromEntity).orElse(null);

        //Todo 사용브랜치에 전역예외처리 클래스가 없어 추후 예외처리할 예정
    }

    public UserResponseDto register(UserRequestDto user) {

        Users users = new Users(
                null,
                user.email(),
                user.nickname(),
                null,
                user.password(),
                null,
                null
        );
        userRepository.save(users);
        return new UserResponseDto(jwtProvider.generateToken(users));

    }

    public UserUpdateResponseDto updateUser(Long id, UserUpdataRequestDto dto) {
        Users user = userRepository.findById(id).orElse(null);
        user.setNickname(dto.nickname());
        userRepository.save(user);
        return new UserUpdateResponseDto(dto.nickname());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

}
