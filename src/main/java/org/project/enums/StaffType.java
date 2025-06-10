package org.project.enums;

public enum StaffType {
    PART_TIME_CONTRACT("PART_TIME_CONTRACT"),
    INTERN("INTERN"),
    CONSULTANT("CONSULTANT"),
    FULL_TIME("FULL_TIME");


    private final String value;

    StaffType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

}
