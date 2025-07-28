package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
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
@Table(name = "supplier_transactions", schema = "swp391")
@FieldNameConstants
public class SupplierTransactionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplierEntity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_manager_id", nullable = false)
    private InventoryManagerEntity inventoryManagerEntity;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private Timestamp transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierTransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplierTransactionStatus status;

    @Column
    private Timestamp approvedDate;

    @Column(length = 500)
    private String notes;

    @Column
    private Timestamp expectedDeliveryDate;

    // Removed from database mapping due to missing columns
    private transient InventoryManagerEntity createdBy;

    public void setCreatedBy(InventoryManagerEntity createdBy) {
        this.createdBy = createdBy;
    }

    public InventoryManagerEntity getCreatedBy() {
        return createdBy;
    }

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @Column(precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(precision = 15, scale = 2)
    private BigDecimal shippingCost;

    @Column(length = 50)
    private String paymentMethod;

    @Column
    private Timestamp dueDate;

    @Column
    private Timestamp paymentDate;
    
    @Column(name = "recipient", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Hospital'")
    private String recipient;
    
    @Column(name = "stock_out_reason", columnDefinition = "TEXT")
    private String stockOutReason;

    // Marked as transient due to missing column in database
    // @Column(length = 500)
    private transient String rejectionReason;

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // Removed from database mapping due to missing columns
    private transient InventoryManagerEntity approvedBy;

    public void setApprovedBy(InventoryManagerEntity approvedBy) {
        this.approvedBy = approvedBy;
    }

    private transient InventoryManagerEntity rejectedBy;

    public void setRejectedBy(InventoryManagerEntity rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    @OneToMany(mappedBy = "supplierTransactionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierTransactionItemEntity> supplierTransactionItemEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "supplierTransactionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierTransactionInvoiceMappingEntity> transactionInvoiceMappings = new LinkedHashSet<>();
}