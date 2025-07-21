package org.project.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "supplier_transactions")
public class SupplierTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_transaction_id")
    private Long supplierTransactionId;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "supplier_id")
    private Long supplierId;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SupplierTransactionItem> items;

    // Constructors
    public SupplierTransaction() {}

    public SupplierTransaction(LocalDate transactionDate, Long supplierId) {
        this.transactionDate = transactionDate;
        this.supplierId = supplierId;
    }

    // Getters and setters
    public Long getSupplierTransactionId() {
        return supplierTransactionId;
    }

    public void setSupplierTransactionId(Long supplierTransactionId) {
        this.supplierTransactionId = supplierTransactionId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public List<SupplierTransactionItem> getItems() {
        return items;
    }

    public void setItems(List<SupplierTransactionItem> items) {
        this.items = items;
    }
}
