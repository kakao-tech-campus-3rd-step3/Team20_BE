package com.example.kspot.itineraries.exception;

import com.example.kspot.contents.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ItineraryExceptionHandler {

  //Itinerary Id로 여행 계획에 조회 실패한 경우 -> 404 Not Found
  @ExceptionHandler
  public ResponseEntity<ApiResponse<Void>> handleItineraryNotFoundException(ItineraryNotFoundException ex) {
    ApiResponse<Void> response = new ApiResponse<>(404, ex.getMessage(), null);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  //여행 계획 생성중, 잘못된 Location Id로 만들려고 한 경우 -> 404 Not Found
  @ExceptionHandler
  public ResponseEntity<ApiResponse<Void>> handleLocationNotFoundException(LocationNotFoundException ex) {
    ApiResponse<Void> response = new ApiResponse<>(404, ex.getMessage(), null);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }
}
