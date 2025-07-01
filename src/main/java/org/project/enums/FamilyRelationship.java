package org.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyRelationship {
    SELF("Self"),
    WIFE("Wife"),
    FATHER("Father"),
    MOTHER("Mother"),
    HUSBAND("Husband"),
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
}
