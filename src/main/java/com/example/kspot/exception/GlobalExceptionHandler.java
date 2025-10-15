package com.example.kspot.exception;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.email.exception.ExpiredTokenException;
import com.example.kspot.email.exception.TokenAlreadyUsedException;
import com.example.kspot.email.exception.TokenNotFoundException;
import com.example.kspot.itineraries.exception.ItineraryNotFoundException;
import com.example.kspot.itineraries.exception.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<String> handleTokenNotFound(TokenNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //404
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<String> handleExpiredToken(ExpiredTokenException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage()); // 410
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<String> handleTokenAlreadyUsed(TokenAlreadyUsedException e, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
    }

    //런타임 예외 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(500, "런타임 오류가 발생했습니다.", null));
    }

    //최상위 예외처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto<>(500, "서버 내부 오류가 발생했습니다.", null));
    }

}
