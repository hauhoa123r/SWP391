package org.project.enums;

public enum FamilyRelationship {
    SELF("SELF"),
    FATHER("father"),
    MOTHER("mother"),
    BROTHER("brother"),
    SISTER("sister"),
    SON("son"),
    DAUGHTER("daughter"),
    GRANDFATHER("grandfather"),
    GRANDMOTHER("grandmother"),
    CAUSIN("causin"),
    AUNT("aunt"),
    UNCLE("uncle"),
    OTHER("other");

    private final String relationship;

    FamilyRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }
}
