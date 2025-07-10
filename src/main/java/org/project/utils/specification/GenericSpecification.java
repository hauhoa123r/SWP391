package org.project.utils.specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.*;
import org.project.enums.operation.LogicalOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.ResourceUnsupportedException;
import org.project.utils.NumberTypeUtils;
import org.project.utils.PathUtils;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.sort.SortCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope("prototype") // Use prototype scope to allow multiple instances with different sort criteria
@SuppressWarnings({"unchecked", "rawtypes"})
public class GenericSpecification<T> implements Specification<T> {
    private Map<SortCriteria, LogicalOperator> sortCriteriaLogicalOperatorMap = new HashMap<>();
    private Map<SearchCriteria, LogicalOperator> searchCriteriaLogicalOperatorMap = new HashMap<>();
    private NumberTypeUtils numberTypeUtils;
    private PathUtils pathUtils;
    private EntityManager entityManager;

    @Autowired
    public void setNumberTypeUtils(NumberTypeUtils numberTypeUtils) {
        this.numberTypeUtils = numberTypeUtils;
    }

    @Autowired
    public void setPathUtils(PathUtils pathUtils) {
        this.pathUtils = pathUtils;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Predicate getPredicate(@NonNull Root<T> root, CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria, Map<String, Join<?, ?>> joinMap) {
        // Tạo đường dẫn truy cập thuộc tính
        pathUtils.join(root, searchCriteria.getFieldName(), searchCriteria.getJoinType(), joinMap);
        Path<Object> realPath = pathUtils.getRealPath(root, searchCriteria.getFieldName(), joinMap);
        Class<?> javaType = root.getJavaType();

        switch (searchCriteria.getComparisonOperator()) {
            case EQUALS -> {
                return criteriaBuilder.equal(realPath, searchCriteria.getComparedValue());
            }
            case NOT_EQUALS -> {
                return criteriaBuilder.notEqual(realPath, searchCriteria.getComparedValue());
            }
            case GREATER_THAN -> {
                return criteriaBuilder.greaterThan(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN -> {
                return criteriaBuilder.lessThan(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                return criteriaBuilder.greaterThanOrEqualTo(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                return criteriaBuilder.lessThanOrEqualTo(pathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LIKE -> {
                return criteriaBuilder.like(pathUtils.getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case NOT_LIKE -> {
                return criteriaBuilder.notLike(pathUtils.getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case CONTAINS -> {
                return criteriaBuilder.like(pathUtils.getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
            }
            case NOT_CONTAINS -> {
                return criteriaBuilder.notLike(pathUtils.getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
            }
            case IN -> {
                return realPath.in(searchCriteria.getComparedValue());
            }
            case NOT_IN -> {
                return criteriaBuilder.not(realPath.in(searchCriteria.getComparedValue()));
            }
            case IS_NULL -> {
                return criteriaBuilder.isNull(realPath);
            }
            case IS_NOT_NULL -> {
                return criteriaBuilder.isNotNull(realPath);
            }
            case BETWEEN -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return criteriaBuilder.between(
                        pathUtils.getPath(realPath, javaType),
                        (Comparable) values[0],
                        (Comparable) values[1]
                );
            }
            case NOT_BETWEEN -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return criteriaBuilder.not(
                        criteriaBuilder.between(
                                pathUtils.getPath(realPath, javaType),
                                (Comparable) values[0],
                                (Comparable) values[1]
                        )
                );
            }

            // Các phép toán AVG
            case AVG_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case AVG_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.notEqual(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case AVG_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThan(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case AVG_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThan(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case AVG_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case AVG_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }

            // Các phép toán COUNT
            case COUNT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.count(realPath), value));
                return criteriaBuilder.conjunction();
            }
            case COUNT_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                criteriaQuery.having(criteriaBuilder.notEqual(criteriaBuilder.count(realPath), value));
                return criteriaBuilder.conjunction();
            }
            case COUNT_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                criteriaQuery.having(criteriaBuilder.greaterThan(criteriaBuilder.count(realPath), value));
                return criteriaBuilder.conjunction();
            }
            case COUNT_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                criteriaQuery.having(criteriaBuilder.lessThan(criteriaBuilder.count(realPath), value));
                return criteriaBuilder.conjunction();
            }
            case COUNT_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                criteriaQuery.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.count(realPath), value));
                return criteriaBuilder.conjunction();
            }
            case COUNT_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Long value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                criteriaQuery.having(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.count(realPath), value));
                return criteriaBuilder.conjunction();
            }

            // Các phép toán SUM
            case SUM_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case SUM_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.notEqual(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case SUM_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThan(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case SUM_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThan(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case SUM_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case SUM_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }

            // Các phép toán MAX
            case MAX_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MAX_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.notEqual(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MAX_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThan(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MAX_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThan(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MAX_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MAX_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }

            // Các phép toán MIN
            case MIN_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.equal(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MIN_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.notEqual(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MIN_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThan(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MIN_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThan(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MIN_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            case MIN_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = numberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                criteriaQuery.having(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)), value));
                return criteriaBuilder.conjunction();
            }
            default -> throw new ResourceUnsupportedException("Unsupported search criteria: " + searchCriteria);
        }
    }

    private Predicate getPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder, SortCriteria sortCriteria, Map<String, Join<?, ?>> joinMap) {
        pathUtils.join(root, sortCriteria.getFieldName(), sortCriteria.getJoinType(), joinMap);
        Path<Object> realPath = pathUtils.getRealPath(root, sortCriteria.getFieldName(), joinMap);
        Class<?> javaType = root.getJavaType();

        switch (sortCriteria.getAggregationFunction()) {
            case NONE -> {
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(realPath)
                                : criteriaBuilder.desc(realPath)
                );
            }
            case COUNT -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.count(realPath))
                                : criteriaBuilder.desc(criteriaBuilder.count(realPath))
                );
            }
            case SUM -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)))
                );
            }
            case AVG -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)))
                );
            }
            case MAX -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)))
                );
            }
            case MIN -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)))
                );
            }
        }
        return criteriaBuilder.conjunction();
    }

    private <C> Predicate mapToPredicate(@NonNull Root<T> root, CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder, Map<String, Join<?, ?>> joinMap, Map<C, LogicalOperator> criteriaLogicalOperatorMap) {
        Predicate predicate = criteriaBuilder.conjunction();
        for (Map.Entry<C, LogicalOperator> entry : criteriaLogicalOperatorMap.entrySet()) {
            C criteria = entry.getKey();
            LogicalOperator logicalOperator = entry.getValue();
            if (criteria != null && logicalOperator != null) {
                if (criteria instanceof SortCriteria sortCriteria) {
                    if (sortCriteria.getFieldName() == null || sortCriteria.getSortDirection() == null || sortCriteria.getAggregationFunction() == null || sortCriteria.getJoinType() == null) {
                        continue; // Skip if sort criteria is incomplete
                    }
                    Predicate sortPredicate = getPredicate(root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap);
                    if (sortPredicate != null) {
                        predicate = logicalOperator.equals(LogicalOperator.AND)
                                ? criteriaBuilder.and(predicate, sortPredicate)
                                : criteriaBuilder.or(predicate, sortPredicate);
                    }
                } else if (criteria instanceof SearchCriteria searchCriteria) {
                    if (searchCriteria.getFieldName() == null || searchCriteria.getComparisonOperator() == null || searchCriteria.getComparedValue() == null || searchCriteria.getJoinType() == null) {
                        continue; // Skip if search criteria is incomplete
                    }
                    Predicate searchPredicate = getPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap);
                    if (searchPredicate != null) {
                        predicate = logicalOperator.equals(LogicalOperator.AND)
                                ? criteriaBuilder.and(predicate, searchPredicate)
                                : criteriaBuilder.or(predicate, searchPredicate);
                    }
                }
            }
        }
        return predicate;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        Map<String, Join<?, ?>> joinMap = new HashMap<>();
        Predicate searchPredicate = mapToPredicate(root, query, criteriaBuilder, joinMap, searchCriteriaLogicalOperatorMap);
        Predicate sortPredicate = mapToPredicate(root, query, criteriaBuilder, joinMap, sortCriteriaLogicalOperatorMap);
        return criteriaBuilder.and(searchPredicate, sortPredicate);
    }
}

