package com.example.kspot.itineraries.exception;

public class ItineraryNotFoundException extends RuntimeException {

  public ItineraryNotFoundException(String message) {
    super(message);
  }
}
