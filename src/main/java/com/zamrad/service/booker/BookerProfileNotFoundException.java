package com.zamrad.service.booker;

public class BookerProfileNotFoundException extends RuntimeException {
    public BookerProfileNotFoundException() {
    }

    public BookerProfileNotFoundException(String message) {
        super(message);
    }

    public BookerProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookerProfileNotFoundException(Throwable cause) {
        super(cause);
    }

    public BookerProfileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
