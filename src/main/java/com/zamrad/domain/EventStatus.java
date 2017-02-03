package com.zamrad.domain;

public enum EventStatus {
    DRAFT("draft"),
    PENDING("pending"),
    CONFIRMED("confirmed");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
