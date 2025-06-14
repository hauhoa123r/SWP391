package org.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private List<CategoryResponse> categoryEntities;
    private List<ServiceFeatureResponse> serviceEntityServiceFeatureEntities;
    private String serviceEntityDepartmentEntityName;
    private List<ProductAdditionalInfoResponse> productAdditionalInfoEntities;
    private Integer reviewCount;
    private Double averageRating;
}
