package com.zamrad.service.artist;

public class InvalidGenreUpdateException extends RuntimeException {
    public InvalidGenreUpdateException() {
    }

    public InvalidGenreUpdateException(String message) {
        super(message);
    }

    public InvalidGenreUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGenreUpdateException(Throwable cause) {
        super(cause);
    }

    public InvalidGenreUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
