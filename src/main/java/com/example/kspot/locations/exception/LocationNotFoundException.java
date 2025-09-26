package com.example.kspot.locations.exception;

public class LocationNotFoundException extends RuntimeException{
    public LocationNotFoundException(Long id){
        super("ID " + id + "에 해당하는 장소를 찾을 수 없습니다.");
    }
}
