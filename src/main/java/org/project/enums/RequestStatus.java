package org.project.enums;


public enum RequestStatus {
    pending("pending"),
    received("received"),
    processing("processing"),
    completed("completed"),
    rejected("rejected"),
    collected("collected");
    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
