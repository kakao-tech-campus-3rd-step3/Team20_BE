package com.example.kspot.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ArtistNotFoundException.class)
    public ResponseEntity<?> handleArtistNotFoundException(ArtistNotFoundException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError<>(404, e.getMessage() , e.getArtistId()));
    }

    public record ApiResponse<T> (int status, String message, T data) {}
    public record ApiError<T>(int status, String message, T data) {}

}
