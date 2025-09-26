package com.example.kspot.artists.service;

import com.example.kspot.artists.dto.ArtistsResponseDto;
import com.example.kspot.artists.repository.ArtistsRepository;
import com.example.kspot.global.exception.ArtistNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

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
                        () -> new ArtistNotFoundException(id)
                )
        );
    }

}
