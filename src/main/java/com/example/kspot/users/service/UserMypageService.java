package com.example.kspot.users.service;

import com.example.kspot.auth.jwt.JwtProvider;
import com.example.kspot.itineraries.dto.ItineraryResponseDto;
import com.example.kspot.itineraries.entity.Itinerary;
import com.example.kspot.itineraries.service.ItineraryService;
import com.example.kspot.users.dto.MypageRequestDto;
import com.example.kspot.users.dto.MypageResponseDto;
import com.example.kspot.users.entity.Users;
import com.example.kspot.users.exception.NotFoundUserException;
import com.example.kspot.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMypageService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final ItineraryService itineraryService;

    public MypageResponseDto getMypage(Long userId){

        Users user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundUserException()
        );

        List<ItineraryResponseDto> list = itineraryService.getItinerariesByUserId(userId);
        return new MypageResponseDto(user.getEmail() , user.getNickname(), list);

    }
}
