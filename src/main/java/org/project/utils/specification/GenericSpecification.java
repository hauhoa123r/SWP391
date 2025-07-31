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
import java.util.LinkedList;
import java.util.List;
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
    private EntityManager entityManager;
    private List<HavingCondition> havingConditions = new LinkedList<>();

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Predicate getPredicate(@NonNull Root<T> root, CriteriaQuery<?> criteriaQuery, @NonNull CriteriaBuilder criteriaBuilder, SearchCriteria searchCriteria, Map<String, Join<?, ?>> joinMap) {
        // Tạo đường dẫn truy cập thuộc tính
        PathUtils.join(root, searchCriteria.getFieldName(), searchCriteria.getJoinType(), joinMap);
        Path<Object> realPath = PathUtils.getRealPath(root, searchCriteria.getFieldName(), joinMap);
        Class<?> javaType = root.getJavaType();

        switch (searchCriteria.getComparisonOperator()) {
            case EQUALS -> {
                return criteriaBuilder.equal(realPath, searchCriteria.getComparedValue());
            }
            case NOT_EQUALS -> {
                return criteriaBuilder.notEqual(realPath, searchCriteria.getComparedValue());
            }
            case GREATER_THAN -> {
                return criteriaBuilder.greaterThan(PathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN -> {
                return criteriaBuilder.lessThan(PathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                return criteriaBuilder.greaterThanOrEqualTo(PathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                return criteriaBuilder.lessThanOrEqualTo(PathUtils.getPath(realPath, javaType), (Comparable) searchCriteria.getComparedValue());
            }
            case LIKE -> {
                return criteriaBuilder.like(PathUtils.getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case NOT_LIKE -> {
                return criteriaBuilder.notLike(PathUtils.getPath(realPath, javaType), searchCriteria.getComparedValue().toString());
            }
            case CONTAINS -> {
                return criteriaBuilder.like(PathUtils.getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
            }
            case NOT_CONTAINS -> {
                return criteriaBuilder.notLike(PathUtils.getPath(realPath, javaType), "%" + searchCriteria.getComparedValue() + "%");
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
                return criteriaBuilder.between(PathUtils.getPath(realPath, javaType), (Comparable) values[0], (Comparable) values[1]);
            }
            case NOT_BETWEEN -> {
                Object[] values = (Object[]) searchCriteria.getComparedValue();
                return criteriaBuilder.not(criteriaBuilder.between(PathUtils.getPath(realPath, javaType), (Comparable) values[0], (Comparable) values[1]));
            }

            // Các phép toán AVG
            case AVG_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.equal(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case AVG_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.notEqual(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case AVG_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThan(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case AVG_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThan(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case AVG_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case AVG_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }

// Các phép toán COUNT
            case COUNT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.equal(criteriaBuilder.count(realPath), value)).build());
                return null;
            }
            case COUNT_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.notEqual(criteriaBuilder.count(realPath), value)).build());
                return null;
            }
            case COUNT_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThan(criteriaBuilder.count(realPath), value)).build());
                return null;
            }
            case COUNT_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThan(criteriaBuilder.count(realPath), value)).build());
                return null;
            }
            case COUNT_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.count(realPath), value)).build());
                return null;
            }
            case COUNT_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Long value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Long.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.count(realPath), value)).build());
                return null;
            }

// Các phép toán SUM
            case SUM_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.equal(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case SUM_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.notEqual(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case SUM_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThan(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case SUM_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThan(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case SUM_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case SUM_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }

// Các phép toán MAX
            case MAX_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.equal(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MAX_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.notEqual(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MAX_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThan(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MAX_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThan(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MAX_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MAX_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.max(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }

// Các phép toán MIN
            case MIN_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.equal(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MIN_NOT_EQUALS -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.notEqual(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MIN_GREATER_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThan(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MIN_LESS_THAN -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThan(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MIN_GREATER_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            case MIN_LESS_THAN_OR_EQUAL_TO -> {
                criteriaQuery.groupBy(root);
                Double value = NumberTypeUtils.convertNumberValue(searchCriteria.getComparedValue(), Double.class);
                havingConditions.add(HavingCondition.builder().predicate(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.min(PathUtils.getPath(realPath, javaType)), value)).build());
                return null;
            }
            default -> throw new ResourceUnsupportedException("Unsupported search criteria: " + searchCriteria);
        }
    }

    private Predicate getPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder, SortCriteria sortCriteria, Map<String, Join<?, ?>> joinMap) {
        PathUtils.join(root, sortCriteria.getFieldName(), sortCriteria.getJoinType(), joinMap);
        Path<Object> realPath = PathUtils.getRealPath(root, sortCriteria.getFieldName(), joinMap);
        Class<?> javaType = root.getJavaType();

        switch (sortCriteria.getAggregationFunction()) {
            case NONE -> {
                query.orderBy(sortCriteria.getSortDirection().equals(SortDirection.ASC) ? criteriaBuilder.asc(realPath) : criteriaBuilder.desc(realPath));
            }
            case COUNT -> {
                query.groupBy(root);
                query.orderBy(sortCriteria.getSortDirection().equals(SortDirection.ASC) ? criteriaBuilder.asc(criteriaBuilder.count(realPath)) : criteriaBuilder.desc(criteriaBuilder.count(realPath)));
            }
            case SUM -> {
                query.groupBy(root);
                query.orderBy(sortCriteria.getSortDirection().equals(SortDirection.ASC) ? criteriaBuilder.asc(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType))) : criteriaBuilder.desc(criteriaBuilder.sum(PathUtils.getPath(realPath, javaType))));
            }
            case AVG -> {
                query.groupBy(root);
                query.orderBy(sortCriteria.getSortDirection().equals(SortDirection.ASC) ? criteriaBuilder.asc(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType))) : criteriaBuilder.desc(criteriaBuilder.avg(PathUtils.getPath(realPath, javaType))));
            }
            case MAX -> {
                query.groupBy(root);
                query.orderBy(sortCriteria.getSortDirection().equals(SortDirection.ASC) ? criteriaBuilder.asc(criteriaBuilder.max(PathUtils.getPath(realPath, javaType))) : criteriaBuilder.desc(criteriaBuilder.max(PathUtils.getPath(realPath, javaType))));
            }
            case MIN -> {
                query.groupBy(root);
                query.orderBy(sortCriteria.getSortDirection().equals(SortDirection.ASC) ? criteriaBuilder.asc(criteriaBuilder.min(PathUtils.getPath(realPath, javaType))) : criteriaBuilder.desc(criteriaBuilder.min(PathUtils.getPath(realPath, javaType))));
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
                    if (sortCriteria.getFieldName() == null || sortCriteria.getSortDirection() == null || sortCriteria.getAggregationFunction() == null) {
                        continue; // Skip if sort criteria is incomplete
                    }
                    Predicate sortPredicate = getPredicate(root, criteriaQuery, criteriaBuilder, sortCriteria, joinMap);
                    if (sortPredicate != null) {
                        predicate = logicalOperator.equals(LogicalOperator.AND) ? criteriaBuilder.and(predicate, sortPredicate) : criteriaBuilder.or(predicate, sortPredicate);
                    }
                } else if (criteria instanceof SearchCriteria searchCriteria) {
                    if (searchCriteria.getFieldName() == null || searchCriteria.getComparisonOperator() == null || searchCriteria.getComparedValue() == null) {
                        continue; // Skip if search criteria is incomplete
                    }
                    int havingPredicateCount = havingConditions.size();
                    Predicate searchPredicate = getPredicate(root, criteriaQuery, criteriaBuilder, searchCriteria, joinMap);
                    if (searchPredicate != null) {
                        predicate = logicalOperator.equals(LogicalOperator.AND) ? criteriaBuilder.and(predicate, searchPredicate) : criteriaBuilder.or(predicate, searchPredicate);
                    } else {
                        if (havingConditions.size() != havingPredicateCount) {
                            HavingCondition havingCondition = havingConditions.get(havingConditions.size() - 1);
                            havingCondition.logicalOperator = logicalOperator;
                            havingConditions.set(havingConditions.size() - 1, havingCondition);
                        }
                    }
                }
            }
        }
        return predicate;
    }

    private Predicate buildHavingPredicate() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate havingPredicate = criteriaBuilder.conjunction();
        for (HavingCondition condition : havingConditions) {
            if (condition.getPredicate() != null) {
                if (condition.getLogicalOperator() == LogicalOperator.AND) {
                    havingPredicate = criteriaBuilder.and(havingPredicate, condition.getPredicate());
                } else if (condition.getLogicalOperator() == LogicalOperator.OR) {
                    havingPredicate = criteriaBuilder.or(havingPredicate, condition.getPredicate());
                } else {
                    throw new ResourceUnsupportedException("Unsupported logical operator in having condition: " + condition.getLogicalOperator());
                }
            }
        }
        return havingPredicate;
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        Map<String, Join<?, ?>> joinMap = new HashMap<>();
        havingConditions = new LinkedList<>();
        Predicate searchPredicate = mapToPredicate(root, query, criteriaBuilder, joinMap, searchCriteriaLogicalOperatorMap);
        Predicate sortPredicate = mapToPredicate(root, query, criteriaBuilder, joinMap, sortCriteriaLogicalOperatorMap);
        Predicate havingPredicate = buildHavingPredicate();
        if (havingPredicate != null) {
            query.having(havingPredicate);
        }
        return criteriaBuilder.and(searchPredicate, sortPredicate);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class HavingCondition {
        private Predicate predicate;
        private LogicalOperator logicalOperator;
    }
}

