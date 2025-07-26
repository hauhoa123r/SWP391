package org.project.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO chứa thông tin tóm tắt về sản phẩm cho dashboard
 */
@Data
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private Integer inStock;
    private Integer soldQuantity;
    private LocalDate transactionDate;
    private BigDecimal totalAmount;
} 