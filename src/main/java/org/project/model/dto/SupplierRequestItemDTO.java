package org.project.model.dto;

import jakarta.validation.constraints.Min;
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
public class SupplierRequestItemDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String productName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal unitPrice;

    private Date manufactureDate;

    private Date expirationDate;

    @Size(max = 255, message = "Batch number cannot exceed 255 characters")
    private String batchNumber;

    @Size(max = 255, message = "Storage location cannot exceed 255 characters")
    private String storageLocation;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}