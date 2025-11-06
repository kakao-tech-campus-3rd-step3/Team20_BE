package com.example.kspot.users.service;

import com.example.kspot.AiItineraries.dto.AiItineraryListResponse;
import com.example.kspot.AiItineraries.service.AiItineraryService;
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
    private final ItineraryService itineraryService;
    private final AiItineraryService aiItineraryService;

    public MypageResponseDto getMypage(Long userId){

        Users user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundUserException()
        );

        List<AiItineraryListResponse> aiList = aiItineraryService.getUserItineraries(userId , false);
        List<ItineraryResponseDto> list = itineraryService.getItinerariesByUserId(userId);
        return new MypageResponseDto(user.getEmail() , user.getNickname(), aiList, list);

    }
}
