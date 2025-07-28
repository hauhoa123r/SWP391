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
    private String recipient;
    private String stockOutReason;
    private Set<SupplierTransactionInvoiceMappingDTO> transactionInvoiceMappings;

    private BigDecimal discountAmount;
    private Timestamp transactionDate;

    private String createdByName;

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setStatus(SupplierTransactionStatus status) {
        this.status = status;
    }
}