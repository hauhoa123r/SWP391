package org.project.enums;

public enum AppoinmentType {
    ONE_SHOT("One-Shot"),
    ASYNC("Async"),
    ;
    private final String value;
    AppoinmentType(String value) {
        this.value = value;
    }
    public String getValue() {return value;}
}
