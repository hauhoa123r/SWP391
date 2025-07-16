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
    REJECTED("rejected"),
    
    // Stock In Flow
    VERIFIED("verified"), // After receiving and verifying stock
    WAITING_FOR_DELIVERY("waiting_for_delivery"),
    INSPECTED("inspected"),
    STOCKED("stocked"),
    PARTIALLY_COMPLETED("partially_completed"),
    
    // Stock Out Flow
    PREPARING("preparing"), // Warehouse staff preparing items
    READY_FOR_SHIPPING("ready_for_shipping"), // Items ready for pickup/shipping
    PARTIALLY_READY("partially_ready"), // Some items ready, some not available
    SHIPPED("shipped"), // Items have been shipped
    DELIVERED("delivered"), // Items have been delivered
    
    // General
    PREPARING_INVOICE("preparing_invoice"),
    SHIPPED_OUT("shipped_out"),
    PROBLEM("problem");

    private final String value;
} 