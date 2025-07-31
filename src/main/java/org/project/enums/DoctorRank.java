package org.project.enums;

import lombok.Getter;

@Getter

public enum DoctorRank {
    INTERN("Thực tập sinh"),
    RESIDENT("Nội trú"),
    ATTENDING("Điều trị"),
    SPECIALIST("Chuyên khoa"),
    SENIOR_SPECIALIST("Chuyên khoa cao cấp"),
    CONSULTANT("Tư vấn"),
    CHIEF_PHYSICIAN("Trưởng khoa");

    private final String rank;

    DoctorRank(String rank) {
        this.rank = rank;
    }
}
