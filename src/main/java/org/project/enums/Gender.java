package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String gender;

    Gender(String gender) {
        this.gender = gender;
    }
}