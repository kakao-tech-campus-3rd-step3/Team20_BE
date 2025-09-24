package com.example.kspot.contents.dto;

import com.example.kspot.artists.dto.ArtistsResponseDto;
import com.example.kspot.artists.entity.Artists;
import com.example.kspot.contents.entity.Content;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContentDetailResponse {
  private Long contentId;
  private String category;
  private String title;
  private String posterImageUrl;
  private LocalDateTime releaseDate;
  private List<ArtistsResponseDto> artists;

  public static ContentDetailResponse fromEntity(Content content){
    return new ContentDetailResponse(
        content.getContent_id(),
        content.getCategory(),
        content.getTitle(),
        content.getPoster_image_url(),
        content.getRelease_date(),
        content.getArtists().stream()
                .map(ArtistsResponseDto::fromEntity).toList()
    );
  }
}
