package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Double rating;
    private String label; // ProductLabel (NEW, SALE, ...)
    private String status; // ACTIVE, INACTIVE
    private Integer stockQuantity;
    private String productType;
    private String category ; // chỉ là tên
    private List<String> tags; // Danh sách tên tag
    private String imageUrl;
}
