package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DoctorRank {
    INTERN("Intern"),
    RESIDENT("Resident"),
    ATTENDING("Attending"),
    SPECIALIST("Specialist"),
    SENIOR_SPECIALIST("Senior Specialist"),
    CONSULTANT("Consultant"),
    CHIEF_PHYSICIAN("Chief Physician");

    private final String rank;
}
