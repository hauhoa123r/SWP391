package org.project.enums;

public enum UserRole {
    PATIENT("PATIENT"),
    STAFF("STAFF"),
    ADMIN("ADMIN");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
