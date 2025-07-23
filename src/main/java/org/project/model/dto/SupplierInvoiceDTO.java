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
public class SupplierInvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private SupplierTransactionType transactionType;
    private Timestamp invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal shippingCost;
    private BigDecimal grandTotal;
    private SupplierTransactionStatus status;
    private Long createdById;
    private String notes;
    private String paymentMethod;
    private Timestamp dueDate;
    private Timestamp paymentDate;
    private Set<SupplierTransactionInvoiceMappingDTO> transactionInvoiceMappings;
}