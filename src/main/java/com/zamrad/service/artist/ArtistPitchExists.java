package com.zamrad.service.artist;

public class ArtistPitchExists extends RuntimeException {
    public ArtistPitchExists(String s) {
    }

    public ArtistPitchExists(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtistPitchExists(Throwable cause) {
        super(cause);
    }

    public ArtistPitchExists(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ArtistPitchExists() {
    }
}
