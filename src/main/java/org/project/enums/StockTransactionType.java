package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockTransactionType {
    STOCK_IN("stock_in"),
    STOCK_OUT("stock_out");

    private final String value;
} 