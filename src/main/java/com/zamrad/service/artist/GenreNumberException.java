package com.zamrad.service.artist;

/**
 * Created by dimitrovm on 12/11/2016.
 */
public class GenreNumberException extends RuntimeException {
    public GenreNumberException() {
    }

    public GenreNumberException(String message) {
        super(message);
    }

    public GenreNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenreNumberException(Throwable cause) {
        super(cause);
    }

    public GenreNumberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
