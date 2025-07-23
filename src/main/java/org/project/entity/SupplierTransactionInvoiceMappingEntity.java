package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "supplier_transaction_invoice_mappings")
public class SupplierTransactionInvoiceMappingEntity {
    @EmbeddedId
    private SupplierTransactionInvoiceMappingEntityId id;

    @MapsId("supplierTransactionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_transaction_id")
    private SupplierTransactionsEntity supplierTransactionEntity;

    @MapsId("supplierInvoiceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_invoice_id")
    private SupplierInvoiceEntity supplierInvoiceEntity;

    @Column(precision = 10, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(length = 255)
    private String notes;
}