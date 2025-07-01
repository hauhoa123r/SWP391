package org.project.utils.filter;

import jakarta.persistence.criteria.*;
import lombok.*;
import org.project.exception.ResourceUnsupportedException;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class GenericSpecification<T> implements Specification<T> {
    private SearchCriteria searchCriteria;

    private <NumberType extends Number> NumberType convertNumberValue(Object value, Class<NumberType> targetType) {
        if (value == null) return null;

        Number numValue = (Number) value;
        if (targetType == Double.class) {
            return (NumberType) Double.valueOf(numValue.doubleValue());
        } else if (targetType == Long.class) {
            return (NumberType) Long.valueOf(numValue.longValue());
        } else if (targetType == Integer.class) {
            return (NumberType) Integer.valueOf(numValue.intValue());
        } else if (targetType == Float.class) {
            return (NumberType) Float.valueOf(numValue.floatValue());
        }

        return (NumberType) numValue;
    }

    private <Y> Path<Y> getPath(Path<?> path, Class<?> javaType) {
        if (javaType == String.class) {
            return (Path<Y>) path.as(String.class);
        } else if (javaType == Integer.class) {
            return (Path<Y>) path.as(Integer.class);
        } else if (javaType == Long.class) {
            return (Path<Y>) path.as(Long.class);
        } else if (javaType == Double.class) {
            return (Path<Y>) path.as(Double.class);
        } else if (javaType == Float.class) {
            return (Path<Y>) path.as(Float.class);
        } else if (javaType == BigDecimal.class)
            return (Path<Y>) path.as(BigDecimal.class);
        return (Path<Y>) path;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        // Tạo đường dẫn truy cập thuộc tính
        Path<Object> realPath;
        Class<?> javaType = root.getJavaType();

        if (searchCriteria.getFieldName().contains(".")) {
            String[] fieldParts = searchCriteria.getFieldName().split("\\.");
            realPath = root.get(fieldParts[0]);
            for (int i = 1; i < fieldParts.length; i++) {
                realPath = realPath.get(fieldParts[i]);
            }
        } else {
            realPath = root.get(searchCriteria.getFieldName());
        }

        switch (searchCriteria.getOperation()) {
            case EQUALS -> {
                return builder.equal(realPath, searchCriteria.getComparedValue());
            }
            case NOT_EQUALS -> {
                return builder.notEqual(realPath, searchCriteria.getComparedValue());
            }
            case GREATER_THAN -> {
                return builder.greaterThan(getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN -> {
                return builder.lessThan(getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                return builder.greaterThanOrEqualTo(getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                return builder.lessThanOrEqualTo(getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LIKE -> {
                return builder.like(getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case NOT_LIKE -> {
                return builder.notLike(getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case CONTAINS -> {
                return builder.like(getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
            }
            case NOT_CONTAINS -> {
                return builder.notLike(getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
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
                        getPath(realPath, javaType),
                        (Comparable) values[0],
                        (Comparable) values[1]
                );
            }
            case NOT_BETWEEN -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return builder.not(
                        builder.between(
                                getPath(realPath, javaType),
                                (Comparable) values[0],
                                (Comparable) values[1]
                        )
                );
            }

            // Các phép toán AVG
            case AVG_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.avg(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.avg(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_GREATER_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.avg(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_LESS_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.avg(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.avg(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case AVG_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.avg(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }

            // Các phép toán COUNT
            case COUNT_EQUALS -> {
                query.groupBy(root);
                Long value = convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.equal(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_NOT_EQUALS -> {
                query.groupBy(root);
                Long value = convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.notEqual(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_GREATER_THAN -> {
                query.groupBy(root);
                Long value = convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.greaterThan(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_LESS_THAN -> {
                query.groupBy(root);
                Long value = convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.lessThan(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Long value = convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.greaterThanOrEqualTo(builder.count(realPath), value));
                return builder.conjunction();
            }
            case COUNT_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Long value = convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                query.having(builder.lessThanOrEqualTo(builder.count(realPath), value));
                return builder.conjunction();
            }

            // Các phép toán SUM
            case SUM_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.sum(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.sum(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_GREATER_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.sum(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_LESS_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.sum(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.sum(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case SUM_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.sum(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }

            // Các phép toán MAX
            case MAX_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.max(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.max(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_GREATER_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.max(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_LESS_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.max(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.max(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MAX_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.max(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }

            // Các phép toán MIN
            case MIN_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.equal(builder.min(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_NOT_EQUALS -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.notEqual(builder.min(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_GREATER_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThan(builder.min(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_LESS_THAN -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThan(builder.min(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_GREATER_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.greaterThanOrEqualTo(builder.min(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            case MIN_LESS_THAN_OR_EQUAL_TO -> {
                query.groupBy(root);
                Double value = convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                query.having(builder.lessThanOrEqualTo(builder.min(getPath(realPath, javaType)), value));
                return builder.conjunction();
            }
            default -> throw new ResourceUnsupportedException("Unsupported search criteria: " + searchCriteria);
        }
    }
}