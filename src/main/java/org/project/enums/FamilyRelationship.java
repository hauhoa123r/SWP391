package org.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyRelationship {
    SELF("Self"),
<<<<<<< HEAD
=======
    WIFE("Wife"),
    HUSBAND("Husband"),
>>>>>>> 1fe7b34939d6bcd04ae5de38ce13891189c4ebc0
    FATHER("Father"),
    MOTHER("Mother"),
    HUSBAND("Husband"),
    WIFE("Wife"),
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
