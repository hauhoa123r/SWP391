package org.project.enums;

import lombok.Getter;

@Getter

public enum UserStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Không hoạt động");

    private final String status;
    UserStatus(String status) {
        this.status = status;
    }
}
