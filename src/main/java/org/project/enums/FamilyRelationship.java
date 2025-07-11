package org.project.enums;

import lombok.Getter;

@Getter
public enum FamilyRelationship {
    SELF("Self"),
    WIFE("Wife"),
    FATHER("Father"),
    MOTHER("Mother"),
    BROTHER("Brother"),
    SISTER("Sister"),
    SON("Son"),
    DAUGHTER("Daughter"),
    GRAND_FATHER("Grandfather"),
    GRAND_MOTHER("Grandmother"),
    COUSIN("Cousin"),
    AUNT("Aunt"),
    UNCLE("Uncle"),
    OTHER("Other");

    private final String relationship;

    FamilyRelationship(String relationship) {
        this.relationship = relationship;
    }
}
