package org.project.enums;

import lombok.Getter;

@Getter
public enum FamilyRelationship {
    SELF("Self"),
    FATHER("Father"),
    MOTHER("Mother"),
    HUSBAND("Husband"),
    BROTHER("Brother"),
    SISTER("Sister"),
    WIFE("Wife"),
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
