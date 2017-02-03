package com.zamrad.service.event;

public class UnauthorisedEventCreationException extends RuntimeException {
    public UnauthorisedEventCreationException() {
    }

    public UnauthorisedEventCreationException(String message) {
        super(message);
    }

    public UnauthorisedEventCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorisedEventCreationException(Throwable cause) {
        super(cause);
    }

    public UnauthorisedEventCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
