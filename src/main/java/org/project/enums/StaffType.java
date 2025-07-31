package org.project.enums;

import lombok.Getter;

@Getter

public enum StaffType {
    PART_TIME_CONTRACT("Hợp đồng bán thời gian"),
    INTERN("Thực tập sinh"),
    CONSULTANT("Tư vấn viên"),
    FULL_TIME("Toàn thời gian");

    private final String value;

    StaffType(String value) {
        this.value = value;
    }
}
