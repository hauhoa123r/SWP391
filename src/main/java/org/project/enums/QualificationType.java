package org.project.enums;

public enum QualificationType {
    DEGREE("DEGREE"),
    CERTIFICATE("CERTIFICATE"),
    LICENSE("LICENSE"),
    AWARD("AWARD");

    private final String value;

    QualificationType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
