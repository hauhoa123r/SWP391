package org.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyRelationship {
    SELF("Self"),
    WIFE("Wife"),
    HUSBAND("Husband"),
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
}
