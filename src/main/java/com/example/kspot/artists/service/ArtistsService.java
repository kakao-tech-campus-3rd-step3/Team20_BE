package com.example.kspot.artists.service;

import com.example.kspot.artists.dto.ArtistsResponseDto;
import com.example.kspot.artists.entity.Artists;
import com.example.kspot.artists.repository.ArtistsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.kspot.artists.dto.ArtistsResponseDto.fromEntity;

@Service
public class ArtistsService {

    private final ArtistsRepository artistsRepository;

    public ArtistsService(ArtistsRepository artistsRepository) {
        this.artistsRepository = artistsRepository;
    }

    public List<ArtistsResponseDto> getArtists() {
        return artistsRepository.findAll().stream().map(ArtistsResponseDto::fromEntity).toList();
    }

    public ArtistsResponseDto getArtistById(Long id) {
        return fromEntity(
                artistsRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("id값이 옳지 않습니다.")
                )
        );
    }

}
