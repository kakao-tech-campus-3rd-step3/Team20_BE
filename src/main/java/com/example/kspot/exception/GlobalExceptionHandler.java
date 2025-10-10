package com.example.kspot.exception;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.itineraries.exception.ItineraryNotFoundException;
import com.example.kspot.itineraries.exception.LocationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Itinerary Id로 여행 계획에 조회 실패한 경우 -> 404 Not Found
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto<Void>> handleItineraryNotFoundException(ItineraryNotFoundException ex) {
        ApiResponseDto<Void> response = new ApiResponseDto<>(404, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //여행 계획 생성중, 잘못된 Location Id로 만들려고 한 경우 -> 404 Not Found
    @ExceptionHandler
    public ResponseEntity<ApiResponseDto<Void>> handleLocationNotFoundException(LocationNotFoundException ex) {
        ApiResponseDto<Void> response = new ApiResponseDto<>(404, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormat(NumberFormatException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id는 숫자여야 합니다."); //400
    }

    @ExceptionHandler(com.example.kspot.locations.exception.LocationNotFoundException.class)
    public ResponseEntity<String> handleNotFound(com.example.kspot.locations.exception.LocationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //404
    }



}
