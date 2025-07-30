package org.project.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.operation.SortDirection;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private Long id;
    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String productEntityName;
    @NotNull(message = "Phòng ban không được để trống")
    private Long departmentEntityId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long minStarRating;
    private Long minReviewCount;
    private String productEntityDescription;
    private String productEntityImageUrl;
    @Positive(message = "Giá dịch vụ phải là một số dương")
    private BigDecimal productEntityPrice;
    @Valid
    private List<ProductAdditionalInfoDTO> productEntityProductAdditionalInfoEntities;
    @Valid
    private List<ServiceFeatureDTO> serviceFeatureEntities;
    private String sortFieldName;
    private SortDirection sortDirection;
}
