package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;
}
