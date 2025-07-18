package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.SupplierTransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stock_requests")
public class StockRequestEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    
    @Column(name = "request_date", nullable = false)
    private Timestamp requestDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private SupplierTransactionType transactionType;
    
    @Column(name = "invoice_number")
    private String invoiceNumber;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private SupplierEntity supplier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_manager_id")
    private InventoryManagerEntity inventoryManager;
    
    @OneToMany(mappedBy = "stockRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockRequestItemEntity> stockRequestItems = new ArrayList<>();
} 