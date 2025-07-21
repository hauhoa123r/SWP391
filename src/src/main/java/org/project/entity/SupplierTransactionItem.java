package org.project.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "supplier_transaction_items")
@IdClass(SupplierTransactionItemId.class)
public class SupplierTransactionItem {

    @Id
    @Column(name = "supplier_transaction_id")
    private Long supplierTransactionId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_transaction_id", insertable = false, updatable = false)
    private SupplierTransaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    // Constructors
    public SupplierTransactionItem() {
    }

    public SupplierTransactionItem(Long supplierTransactionId, Long productId, int quantity, BigDecimal unitPrice) {
        this.supplierTransactionId = supplierTransactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public Long getSupplierTransactionId() {
        return supplierTransactionId;
    }

    public void setSupplierTransactionId(Long supplierTransactionId) {
        this.supplierTransactionId = supplierTransactionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public SupplierTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(SupplierTransaction transaction) {
        this.transaction = transaction;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
