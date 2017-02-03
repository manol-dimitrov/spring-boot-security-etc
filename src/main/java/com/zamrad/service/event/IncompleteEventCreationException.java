package com.zamrad.service.event;

public class IncompleteEventCreationException extends RuntimeException {
    public IncompleteEventCreationException() {
    }

    public IncompleteEventCreationException(String message) {
        super(message);
    }

    public IncompleteEventCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncompleteEventCreationException(Throwable cause) {
        super(cause);
    }

    public IncompleteEventCreationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
