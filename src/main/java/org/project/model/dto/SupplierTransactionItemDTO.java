package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierTransactionItemDTO {
    private Long supplierTransactionId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Date manufactureDate;
    private Date expirationDate;
    private String batchNumber;
    private String storageLocation;
    private String notes;
}