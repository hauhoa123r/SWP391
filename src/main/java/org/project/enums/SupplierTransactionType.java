package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Getter
public enum SupplierTransactionType {
    STOCK_IN("stock_in"),
    STOCK_OUT("stock_out");

    private final String value;
}