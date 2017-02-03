package com.zamrad.service.artist;

public class ProfileAlreadyExistsException extends RuntimeException {
    public ProfileAlreadyExistsException() {
    }

    public ProfileAlreadyExistsException(String message) {
        super(message);
    }

    public ProfileAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public ProfileAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
