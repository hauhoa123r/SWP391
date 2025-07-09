package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.project.enums.StockStatus;
import org.project.enums.StockTransactionType;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stock_requests", schema = "swp391")
public class StockRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_request_id", nullable = false)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'STOCK_IN'")
    @Column(name = "transaction_type", columnDefinition = "enum not null")
    private StockTransactionType transactionType;

    @NotNull
    @Column(name = "request_date", nullable = false)
    private Timestamp requestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by", nullable = false)
    private InventoryManagerEntity requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private InventoryManagerEntity approvedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PENDING'")
    @Column(name = "status", columnDefinition = "enum not null")
    private StockStatus status = StockStatus.PENDING;

    @Column(name = "approved_date")
    private Timestamp approvedDate;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @OneToMany(mappedBy = "stockRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StockRequestItemEntity> stockRequestItems = new LinkedHashSet<>();

    @OneToOne(mappedBy = "stockRequest", cascade = CascadeType.ALL)
    private StockInvoiceEntity stockInvoice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;
    
    @Column(name = "expected_delivery_date")
    private Timestamp expectedDeliveryDate;
} 