package org.project.enums;

import lombok.Getter;

@Getter

public enum DoctorRank {
    INTERN("Intern"),
    RESIDENT("Resident"),
    ATTENDING("Attending"),
    SPECIALIST("Specialist"),
    SENIOR_SPECIALIST("Senior Specialist"),
    CONSULTANT("Consultant"),
    CHIEF_PHYSICIAN("Chief Physician");

    private final String rank;

    DoctorRank(String rank) {
        this.rank = rank;
    }
}
