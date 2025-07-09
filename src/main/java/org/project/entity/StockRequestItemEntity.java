package org.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "stock_request_items", schema = "swp391")
public class StockRequestItemEntity {
    @EmbeddedId
    private StockRequestItemEntityId id;

    @MapsId("stockRequestId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_request_id", nullable = false)
    private StockRequestEntity stockRequest;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "manufacture_date")
    private Date manufactureDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Size(max = 255)
    @Column(name = "batch_number")
    private String batchNumber;

    @Size(max = 255)
    @Column(name = "storage_location")
    private String storageLocation;

    @Size(max = 500)
    @Column(name = "notes")
    private String notes;
} 