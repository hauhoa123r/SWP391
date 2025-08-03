package org.project.enums;

public enum UserRole {
    PATIENT("Bệnh nhân"),
    STAFF("Nhân viên"),
    ADMIN("Quản trị viên");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
