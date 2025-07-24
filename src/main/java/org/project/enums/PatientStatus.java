package org.project.enums;

import lombok.Getter;

@Getter
public enum PatientStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String status;

    PatientStatus(String status) {
        this.status = status;
    }
}
