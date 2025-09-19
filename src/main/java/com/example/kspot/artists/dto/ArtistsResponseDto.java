package com.example.kspot.artists.dto;

import com.example.kspot.artists.entity.Artists;

public record ArtistsResponseDto(
        Long artistId,
        String name,
        String profileImageUrl
) {
    public static ArtistsResponseDto fromEntity(Artists artists) {
        return new ArtistsResponseDto(
                artists.getArtistId(),
                artists.getName(),
                artists.getProfileImageUrl()
        );
    }
}
