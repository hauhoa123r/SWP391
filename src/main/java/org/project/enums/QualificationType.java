package org.project.enums;

import lombok.Getter;

@Getter

public enum QualificationType {
    DEGREE("Bằng cấp"),
    CERTIFICATE("Chứng chỉ"),
    LICENSE("Giấy phép"),
    AWARD("Giải thưởng");

    private final String value;

    QualificationType(String value) {
        this.value = value;
    }
}
