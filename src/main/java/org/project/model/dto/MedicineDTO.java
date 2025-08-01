package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String unit;
    private Integer stockQuantities;
    private String imageUrl;
    private ProductType productType;
    private ProductStatus productStatus;
    private Label label;
    private Double averageRating;
    private Long reviewCount;
    private String category;
    private final Integer minStock = 100;
    private Set<BatchDTO> batches;
} 