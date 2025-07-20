package org.project.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.operation.SortDirection;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private String productEntityName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long minStarRating;
    private String sortFieldName;
    private SortDirection sortDirection;
}
