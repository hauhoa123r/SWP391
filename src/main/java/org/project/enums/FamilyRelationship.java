package org.project.enums;

import lombok.Getter;

@Getter
public enum FamilyRelationship {
    SELF("Bản thân"),
    FATHER("Bố"),
    MOTHER("Mẹ"),
    HUSBAND("Chồng"),
    BROTHER("Anh/Em trai"),
    SISTER("Chị/Em gái"),
    WIFE("Vợ"),
    SON("Con trai"),
    DAUGHTER("Con gái"),
    GRAND_FATHER("Ông"),
    GRAND_MOTHER("Bà"),
    COUSIN("Anh/Chị/Em họ"),
    AUNT("Cô/Dì/Bác gái"),
    UNCLE("Chú/Bác trai"),
    OTHER("Khác");

    private final String relationship;

    FamilyRelationship(String relationship) {
        this.relationship = relationship;
    }
}
