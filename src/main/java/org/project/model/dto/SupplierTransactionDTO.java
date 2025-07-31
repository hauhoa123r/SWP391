package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransactionDTO {
    private Long id;
    private Long supplierId;
    private Long inventoryManagerId;
    private BigDecimal totalAmount;
    private Timestamp transactionDate;
    private SupplierTransactionType transactionType;
    private SupplierTransactionStatus status;
    private Timestamp approvedDate;
    private String notes;
    private Timestamp expectedDeliveryDate;
    private String invoiceNumber;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private String paymentMethod;
    private Timestamp dueDate;
    private Timestamp paymentDate;
    private Set<SupplierTransactionItemDTO> supplierTransactionItemEntities;
    private Set<SupplierTransactionInvoiceMappingDTO> transactionInvoiceMappings;
    private Timestamp invoiceDate;
    private BigDecimal grandTotal;
    private Long createdById;
    private String recipient;
    private String stockOutReason;
    private BigDecimal discountAmount;
    private String createdByName;
}