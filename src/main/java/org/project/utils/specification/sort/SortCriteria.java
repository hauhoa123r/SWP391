package org.project.utils.specification.sort;

import jakarta.persistence.criteria.JoinType;
import lombok.*;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.SortDirection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SortCriteria {
    private String fieldName;
    private AggregationFunction aggregationFunction;
    private SortDirection sortDirection;
    private JoinType joinType;
}
