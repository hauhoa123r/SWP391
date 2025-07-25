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

    // Xóa hoặc đổi tên trường này vì nó không có trong database
    // @ManyToOne
    // @JoinColumn(name = "approved_by_id")
    // private InventoryManagerEntity approvedBy;

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

    @OneToMany(mappedBy = "supplierTransactionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierTransactionItemEntity> supplierTransactionItemEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "supplierTransactionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierTransactionInvoiceMappingEntity> transactionInvoiceMappings = new LinkedHashSet<>();
}