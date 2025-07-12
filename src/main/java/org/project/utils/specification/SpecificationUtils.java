package org.project.utils.specification;

import jakarta.persistence.criteria.JoinType;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.LogicalOperator;
import org.project.enums.operation.SortDirection;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.sort.SortCriteria;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpecificationUtils<T> {
    private ObjectProvider<GenericSpecification<T>> genericSpecificationObjectProvider;
    private Map<SearchCriteria, LogicalOperator> searchCriteriaLogicalOperatorMap;
    private Map<SortCriteria, LogicalOperator> sortCriteriaLogicalOperatorMap;

    @Autowired
    public void setGenericSpecificationObjectProvider(ObjectProvider<GenericSpecification<T>> genericSpecificationObjectProvider) {
        this.genericSpecificationObjectProvider = genericSpecificationObjectProvider;
    }

    public SpecificationUtils<T> addSearchCriteria(SearchCriteria searchCriteria, LogicalOperator logicalOperator) {
        searchCriteriaLogicalOperatorMap.put(searchCriteria, logicalOperator);
        return this;
    }

    public SpecificationUtils<T> addSortCriteria(SortCriteria sortCriteria, LogicalOperator logicalOperator) {
        sortCriteriaLogicalOperatorMap.put(sortCriteria, logicalOperator);
        return this;
    }

    public Specification<T> getSearchSpecification() {
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(new HashMap<>());
        return genericSpecification;
    }

    public Specification<T> getSortSpecification() {
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(new HashMap<>());
        genericSpecification.setSortCriteriaLogicalOperatorMap(this.sortCriteriaLogicalOperatorMap);
        return genericSpecification;
    }

    public Specification<T> getSpecification() {
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(this.sortCriteriaLogicalOperatorMap);
        return genericSpecification;
    }

    public SpecificationUtils<T> reset() {
        this.searchCriteriaLogicalOperatorMap = new HashMap<>();
        this.sortCriteriaLogicalOperatorMap = new HashMap<>();
        return this;
    }

    public Specification<T> getSearchSpecification(SearchCriteria searchCriteria) {
        if (searchCriteria == null || searchCriteria.getFieldName() == null || searchCriteria.getComparisonOperator() == null || searchCriteria.getComparedValue() == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(new HashMap<>());
        return genericSpecification;
    }

    public Specification<T> getSortSpecification(SortCriteria sortCriteria) {
        if (sortCriteria == null || sortCriteria.getFieldName() == null || sortCriteria.getSortDirection() == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(new HashMap<>());
        genericSpecification.setSortCriteriaLogicalOperatorMap(this.sortCriteriaLogicalOperatorMap);
        return genericSpecification;
    }

    public Specification<T> getSearchSpecification(String fieldName, ComparisonOperator comparisonOperator, Object comparedValue, JoinType joinType) {
        if (fieldName == null || comparisonOperator == null || comparedValue == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSortCriteriaLogicalOperatorMap(
                Map.of(
                        SortCriteria.builder()
                                .fieldName(fieldName)
                                .sortDirection(SortDirection.ASC)
                                .joinType(joinType)
                                .build(),
                        LogicalOperator.AND
                )
        );
        genericSpecification.setSearchCriteriaLogicalOperatorMap(this.searchCriteriaLogicalOperatorMap);
        genericSpecification.setSortCriteriaLogicalOperatorMap(new HashMap<>());
        return genericSpecification;
    }

    public Specification<T> getSortSpecification(String fieldName, AggregationFunction aggregationFunction, SortDirection sortDirection, JoinType joinType) {
        if (fieldName == null || aggregationFunction == null || sortDirection == null) {
            return null;
        }
        GenericSpecification<T> genericSpecification = genericSpecificationObjectProvider.getObject();
        genericSpecification.setSearchCriteriaLogicalOperatorMap(new HashMap<>());
        genericSpecification.setSortCriteriaLogicalOperatorMap(
                Map.of(
                        SortCriteria.builder()
                                .fieldName(fieldName)
                                .aggregationFunction(aggregationFunction)
                                .sortDirection(sortDirection)
                                .joinType(joinType)
                                .build(),
                        LogicalOperator.AND
                )
        );
        return genericSpecification;
    }

    public Specification<T> getSearchSpecifications(SearchCriteria... searchCriterias) {
        return getSearchSpecifications(Arrays.asList(searchCriterias));
    }

    public Specification<T> getSortSpecifications(SortCriteria... sortCriterias) {
        return getSortSpecifications(Arrays.asList(sortCriterias));
    }

    public Specification<T> getSearchSpecifications(List<SearchCriteria> searchCriterias) {
        this.searchCriteriaLogicalOperatorMap = new HashMap<>();
        for (SearchCriteria searchCriteria : searchCriterias) {
            this.searchCriteriaLogicalOperatorMap.put(searchCriteria, LogicalOperator.AND);
        }
        return getSearchSpecification();
    }

    public Specification<T> getSortSpecifications(List<SortCriteria> sortCriterias) {
        this.sortCriteriaLogicalOperatorMap = new HashMap<>();
        for (SortCriteria sortCriteria : sortCriterias) {
            this.sortCriteriaLogicalOperatorMap.put(sortCriteria, LogicalOperator.AND);
        }
        return getSortSpecification();
    }

    public Specification<T> getSpecifications(List<SearchCriteria> searchCriterias, List<SortCriteria> sortCriterias) {
        for (SearchCriteria searchCriteria : searchCriterias) {
            this.searchCriteriaLogicalOperatorMap.put(searchCriteria, LogicalOperator.AND);
        }
        for (SortCriteria sortCriteria : sortCriterias) {
            this.sortCriteriaLogicalOperatorMap.put(sortCriteria, LogicalOperator.AND);
        }
        return getSpecification();
    }
}
