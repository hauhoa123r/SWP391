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
import org.project.enums.SupplierTransactionType;
import java.math.BigDecimal;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stock_invoices", schema = "swp391")
public class StockInvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_request_id", nullable = false, unique = true)
    private StockRequestEntity stockRequest;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", columnDefinition = "enum not null")
    private SupplierTransactionType transactionType;

    @NotNull
    @Column(name = "invoice_date", nullable = false)
    private Timestamp invoiceDate;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @NotNull
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'COMPLETED'")
    @Column(name = "status", columnDefinition = "enum not null")
    private StockStatus status = StockStatus.COMPLETED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private InventoryManagerEntity createdBy;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;

    @Size(max = 255)
    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "due_date")
    private Timestamp dueDate;

    @Column(name = "payment_date")
    private Timestamp paymentDate;
} 