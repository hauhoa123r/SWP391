package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public enum UserStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String status;
    UserStatus(String status) {
        this.status = status;
    }
}
