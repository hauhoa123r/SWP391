package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SupplierTransactionType {
    STOCK_IN("Nhập kho"),
    STOCK_OUT("Xuất kho");

    private final String value;
}