package org.project.enums;

import lombok.Getter;

@Getter
public enum PatientStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động");

    private final String status;

    PatientStatus(String status) {
        this.status = status;
    }
}
