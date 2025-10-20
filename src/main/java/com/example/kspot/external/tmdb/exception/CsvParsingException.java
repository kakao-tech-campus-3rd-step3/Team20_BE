package com.example.kspot.external.tmdb.exception;

public class CsvParsingException extends RuntimeException {

  public CsvParsingException(String message) {
    super(message);
  }
  public CsvParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
