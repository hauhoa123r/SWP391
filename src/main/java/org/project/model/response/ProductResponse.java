package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.Label;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String unit;
    private Integer stockQuantity;
    private String imageUrl;
    private ProductType productType;
    private ProductStatus productStatus;
    private Label label;
    private Double averageRating;
    private Long reviewCount;
}
