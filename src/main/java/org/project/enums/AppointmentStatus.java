package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public enum AppointmentStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CONFLICTED("Conflicted");

    private final String status;
    AppointmentStatus(String status) {
        this.status = status;
    }
}
