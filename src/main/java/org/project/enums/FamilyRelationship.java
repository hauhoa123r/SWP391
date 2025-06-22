package org.project.enums;

public enum FamilyRelationship {
    SELF("SELF"),
    FATHER("FATHER"),
    MOTHER("MOTHER"),
    BROTHER("BROTHER"),
    SISTER("SISTER"),
    SON("SON"),
    DAUGHTER("DAUGHTER"),
    GRANDFATHER("GRANDFATHER"),
    GRANDMOTHER("GRANDMOTHER"),
    CAUSIN("CAUSIN"),
    AUNT("AUNT"),
    UNCLE("UNCLE"),
    OTHER("OTHER");

    private final String relationship;

    FamilyRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }
}
