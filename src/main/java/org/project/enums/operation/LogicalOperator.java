package org.project.enums.operation;

public enum LogicalOperator {
    AND("Và"),
    OR("Hoặc");

    private final String operator;

    LogicalOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
