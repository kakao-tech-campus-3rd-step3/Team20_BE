package com.example.kspot.contents.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContentListResponse {
  private List<ContentItemDto> items;
  private PaginationDto pagination;
}
