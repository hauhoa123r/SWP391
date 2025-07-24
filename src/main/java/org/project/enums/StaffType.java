package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffType {
    PART_TIME_CONTRACT("Part Time Contract"),
    INTERN("Intern"),
    CONSULTANT("Consultant"),
    FULL_TIME("Full Time");

    private final String value;
}
