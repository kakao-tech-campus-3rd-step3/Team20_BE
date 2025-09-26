package com.example.kspot.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContentItemDto {
  private Long contentId;
  private String category;
  private String title;
  private String posterImageUrl;
}
