package org.project.enums.operation;

public enum SortDirection {
    ASC("Tăng dần"),
    DESC("Giảm dần");

    private final String direction;

    SortDirection(String direction) {
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }
}
