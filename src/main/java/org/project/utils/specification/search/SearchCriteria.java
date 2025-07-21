package org.project.utils.specification.search;

import jakarta.persistence.criteria.JoinType;
import lombok.*;
import org.project.enums.operation.ComparisonOperator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCriteria {
    private String fieldName;
    private ComparisonOperator comparisonOperator;
    private Object comparedValue;
    private JoinType joinType;
}