package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockInvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private Long stockRequestId;
    private StockTransactionType transactionType;
    private Timestamp invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal grandTotal;
    private StockStatus status;
    private InventoryManagerResponse createdBy;
    private String notes;
    private String paymentMethod;
    private Timestamp dueDate;
    private Timestamp paymentDate;
} 