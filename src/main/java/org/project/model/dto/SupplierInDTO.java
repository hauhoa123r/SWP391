package org.project.model.dto;

import lombok.*;
import org.project.enums.SupplierTransactionStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierInDTO {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private String supplierContact;
    private Long inventoryManagerId;
    private String inventoryManagerName;
    private BigDecimal totalAmount;
    private Timestamp transactionDate;
    private SupplierTransactionStatus status;
    private Timestamp approvedDate;
    private String notes;
    private Timestamp expectedDeliveryDate;
    private Long approvedById;
    private String approvedByName;
    private String invoiceNumber;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private String paymentMethod;
    private Timestamp dueDate;
    private Timestamp paymentDate;
    private List<SupplierRequestItemDTO> items;
    
    // Additional fields for the StockIn view
    private String type;
    private Timestamp createdAt;
} 