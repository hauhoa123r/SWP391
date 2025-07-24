package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public enum QualificationType {
    DEGREE("DEGREE"),
    CERTIFICATE("CERTIFICATE"),
    LICENSE("LICENSE"),
    AWARD("AWARD");

    private final String value;

    QualificationType(String value) {
        this.value = value;
    }
}
