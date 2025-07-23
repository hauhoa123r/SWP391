package org.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing the status of a supplier transaction.
 * Contains statuses for both StockIn and StockOut workflows.
 */
@AllArgsConstructor
@Getter
public enum SupplierTransactionStatus {
    // Common final statuses - display in StockInInvoice/StockOutInvoice based on type
    COMPLETED("Hoàn thành"),             // Final state, transaction is complete
    REJECTED("Từ chối"),                 // Transaction is rejected
    
    // StockIn specific statuses - display in StockIn page
    WAITING_FOR_DELIVERY("Chờ giao hàng"),  // Order placed, waiting for delivery
    INSPECTED("Đã kiểm tra"),               // Products received and inspected
    RECEIVED("Đã nhận hàng"),               // Products received but not yet inspected
    
    // StockOut specific statuses - display in StockOut page
    PREPARE_DELIVERY("Chuẩn bị giao hàng"),  // Products being prepared for delivery
    DELIVERING("Đang giao hàng"),            // Products are being delivered to customer
    DELIVERED("Đã giao hàng"),               // Products are delivered to customer
    PENDING("Chờ thanh toán"),               // Waiting for payment
    PAID("Đã thanh toán"),                   // Payment received
    
    // Order processing statuses
    PREPARING("Chuẩn bị đơn hàng"),         // Order is being prepared
    ADDED("Đã thêm"),                        // Items added to order
    PREPARED("Đã chuẩn bị hàng");           // Order is prepared and ready

    private final String displayName;
}