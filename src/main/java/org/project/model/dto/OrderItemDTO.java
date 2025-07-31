package org.project.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
@Builder
@Data
public class OrderItemDTO {
    private String productName;
    private int quantity;
    private BigDecimal price;
}
