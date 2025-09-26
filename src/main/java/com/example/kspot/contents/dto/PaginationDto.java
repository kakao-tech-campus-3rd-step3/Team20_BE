package com.example.kspot.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaginationDto {
  private int currentPage;
  private int itemPerPage;
  private long totalItems;
  private int totalPages;
}
