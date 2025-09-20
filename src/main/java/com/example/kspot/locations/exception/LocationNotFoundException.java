package com.example.kspot.locations.exception;

public class LocationNotFoundException extends RuntimeException{
    public LocationNotFoundException(Long id){
        super("장소를 찾을 수 없습니다." + id + " not found");
    }
}
