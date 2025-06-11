package org.project.enums;
public enum AppointmentStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    IN_PROGRESS("In Progress"),
    WAITING_RESULT("Waiting Result"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String value;

    AppointmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
