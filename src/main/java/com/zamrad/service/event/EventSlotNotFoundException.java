package com.zamrad.service.event;

public class EventSlotNotFoundException extends RuntimeException {
    public EventSlotNotFoundException() {
    }

    public EventSlotNotFoundException(String message) {
        super(message);
    }

    public EventSlotNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventSlotNotFoundException(Throwable cause) {
        super(cause);
    }

    public EventSlotNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
