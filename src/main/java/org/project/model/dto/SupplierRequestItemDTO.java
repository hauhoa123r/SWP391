package org.project.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.ProductType;

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
    
    // Thêm trường productType và productUnit
    private ProductType productType;
    private String productUnit;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal unitPrice;
    
    // Thêm field totalPrice để tính toán tổng tiền
    private BigDecimal totalPrice;

    private Date manufactureDate;

    private Date expirationDate;

    @Size(max = 255, message = "Storage location cannot exceed 255 characters")
    private String storageLocation;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}