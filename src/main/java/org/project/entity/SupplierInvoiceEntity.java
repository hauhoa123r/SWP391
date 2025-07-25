package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.SupplierTransactionStatus;
import org.project.enums.SupplierTransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "supplier_invoices")
public class SupplierInvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierTransactionType transactionType;

    @Column(nullable = false)
    private Timestamp invoiceDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(precision = 10, scale = 2)
    private BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    private SupplierTransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private InventoryManagerEntity createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplierEntity;

    @Column(length = 500)
    private String notes;

    @Column(length = 50)
    private String paymentMethod;

    @Column
    private Timestamp dueDate;

    @Column
    private Timestamp paymentDate;
    
    @Column(length = 500)
    private String rejectionReason;

    @OneToMany(mappedBy = "supplierInvoiceEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierTransactionInvoiceMappingEntity> transactionInvoiceMappings = new LinkedHashSet<>();
}