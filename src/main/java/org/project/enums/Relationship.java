package org.project.enums;

public enum Relationship {
    SELF("Self"),
    FATHER("Father"),
    MOTHER("Mother"),
    BROTHER("Brother"),
    SISTER("Sister"),
    DAUGHTER("Daughter"),
    SON("Son"),
    GRAND_FATHER("Grand Father"),
    GRAND_MOTHER("Grand Mother"),
    UNCLE("Uncle"),
    AUNT("Aunt"),
    CAUSIN("Causin"),
    OTHER("Other");

    private final String value;

    Relationship(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
