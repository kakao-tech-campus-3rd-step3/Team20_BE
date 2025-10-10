package com.example.kspot.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseDto<T>{
  private int status;
  private String message;
  private T data;
}
