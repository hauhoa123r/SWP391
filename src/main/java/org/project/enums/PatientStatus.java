package org.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PatientStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String status;

}
