package org.project.utils.specification.search;

import jakarta.persistence.criteria.*;
import lombok.*;
import org.project.exception.ResourceUnsupportedException;
import org.project.utils.NumberTypeUtils;
import org.project.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class SearchSpecification<T> implements Specification<T> {
    private SearchCriteria searchCriteria;
    private NumberTypeUtils numberTypeUtils;
    private PathUtils pathUtils;

    @Autowired
    public void setNumberTypeUtils(NumberTypeUtils numberTypeUtils) {
        this.numberTypeUtils = numberTypeUtils;
    }

    @Autowired
    public void setPathUtils(PathUtils pathUtils) {
        this.pathUtils = pathUtils;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        // Tạo đường dẫn truy cập thuộc tính
        Path<Object> realPath = pathUtils.getRealPath(root, searchCriteria.getFieldName(), searchCriteria.getJoinType());
        Class<?> javaType = root.getJavaType();

        switch (searchCriteria.getComparisonOperator()) {
            case EQUALS -> {
                return builder.equal(realPath, searchCriteria.getComparedValue());
            }
            case NOT_EQUALS -> {
                return builder.notEqual(realPath, searchCriteria.getComparedValue());
            }
            case GREATER_THAN -> {
                return builder.greaterThan(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN -> {
                return builder.lessThan(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                return builder.greaterThanOrEqualTo(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                return builder.lessThanOrEqualTo(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LIKE -> {
                return builder.like(pathUtils.getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case NOT_LIKE -> {
                return builder.notLike(pathUtils.getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case CONTAINS -> {
                return builder.like(pathUtils.getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
            }
            case NOT_CONTAINS -> {
                return builder.notLike(pathUtils.getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
            }
            case IN -> {
                return realPath.in(searchCriteria.getComparedValue());
            }
            case NOT_IN -> {
                return builder.not(realPath.in(searchCriteria.getComparedValue()));
            }
            case IS_NULL -> {
                return builder.isNull(realPath);
            }
            case IS_NOT_NULL -> {
                return builder.isNotNull(realPath);
            }
            case BETWEEN -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return builder.between(
                        pathUtils.getPath(realPath, javaType),
                        (Comparable) values[0],
                        (Comparable) values[1]
                );
            }
            case NOT_BETWEEN -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return builder.not(
                        builder.between(
                                pathUtils.getPath(realPath, javaType),
                                (Comparable) values[0],
                                (Comparable) values[1]
                        )
                );
            }

            // Các phép toán AVG
            case AVG_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.avg(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.avg(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_GREATER_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.avg(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_LESS_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.avg(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.avg(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.avg(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }

            // Các phép toán COUNT
            case COUNT_EQUALS -> {
                query.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.equal(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_NOT_EQUALS -> {
                query.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.notEqual(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_GREATER_THAN -> {
                query.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.greaterThan(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_LESS_THAN -> {
                query.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.lessThan(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.greaterThanOrEqualTo(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.lessThanOrEqualTo(builder.count(realPath), value));
                return builder.conjunction();
            }

            // Các phép toán SUM
            case SUM_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.sum(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.sum(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_GREATER_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.sum(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_LESS_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.sum(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.sum(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.sum(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }

            // Các phép toán MAX
            case MAX_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.max(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.max(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_GREATER_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.max(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_LESS_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.max(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.max(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.max(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }

            // Các phép toán MIN
            case MIN_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.min(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.min(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_GREATER_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.min(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_LESS_THAN -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.min(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.min(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.min(pathUtils.getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            default -> throw new ResourceUnsupportedException("Unsupported search criteria: " + searchCriteria);
        }
    }
}