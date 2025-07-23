package org.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "supplier_transaction_items")
public class SupplierTransactionItemEntity {
    @EmbeddedId
    private SupplierTransactionItemEntityId id;

    @MapsId("supplierTransactionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_transaction_id")
    private SupplierTransactionsEntity supplierTransactionEntity;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity productEntity;

    @Column(nullable = false)
    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column
    private Date manufactureDate;

    @Column
    private Date expirationDate;

    @Column(length = 255)
    private String batchNumber;

    @Column(length = 255)
    private String storageLocation;

    @Column(length = 500)
    private String notes;
}