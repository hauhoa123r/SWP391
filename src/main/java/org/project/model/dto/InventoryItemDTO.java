package org.project.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InventoryItemDTO {
    private Long id;
    private String batchNumber;
    private LocalDate expiryDate;
    private Integer quantity;
    private Integer reservedQuantity;
    private BigDecimal unitCost;
    private String status;
}
