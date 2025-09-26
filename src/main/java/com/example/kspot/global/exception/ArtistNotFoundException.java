package com.example.kspot.global.exception;

public class ArtistNotFoundException extends RuntimeException {

    private final Long artistId;

    public ArtistNotFoundException(Long artistId) {
        super("아티스트를 찾지 못했습니다 artistId: " + artistId);
        this.artistId = artistId;
    }

    public ArtistNotFoundException(Long artistId ,String message) {
        super(message);
        this.artistId = artistId;
    }

    public ArtistNotFoundException(Long artistId , String message, Throwable cause) {
        super(message, cause);
        this.artistId = artistId;
    }

    public Long getArtistId() {
        return artistId;
    }

}
