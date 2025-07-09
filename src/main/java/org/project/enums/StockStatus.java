package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockStatus {
    PENDING("pending"),
    APPROVED("approved"),
    PROCESSING("processing"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    SHIPPED("shipped"),
    DELIVERED("delivered"),
    WAITING_FOR_DELIVERY("waiting_for_delivery"),
    INSPECTED("inspected"),
    STOCKED("stocked"),
    PREPARING_INVOICE("preparing_invoice"),
    PREPARING("preparing"),
    READY_FOR_SHIPPING("ready_for_shipping"),
    SHIPPED_OUT("shipped_out"),
    PROBLEM("problem");

    private final String value;
} 