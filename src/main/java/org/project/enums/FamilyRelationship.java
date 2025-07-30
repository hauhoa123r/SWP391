package org.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FamilyRelationship {
    SELF("Bản thân"),
    WIFE("Vợ"),
    HUSBAND("Chồng"),
    FATHER("Bố"),
    MOTHER("Mẹ"),
    BROTHER("Anh / Em trai"),
    SISTER("Chị / Em gái"),
    SON("Con trai"),
    DAUGHTER("Con gái"),
    GRAND_FATHER("Ông"),
    GRAND_MOTHER("Bà"),
    COUSIN("Cháu"),
    AUNT("Dì"),
    UNCLE("Chú"),
    OTHER("Khác");

    private final String relationship;
}
