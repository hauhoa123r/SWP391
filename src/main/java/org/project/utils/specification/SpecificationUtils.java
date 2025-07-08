package org.project.utils.specification;

import jakarta.persistence.criteria.JoinType;
import org.project.enums.operation.AggregationFunction;
import org.project.enums.operation.ComparisonOperator;
import org.project.enums.operation.LogicalOperator;
import org.project.enums.operation.SortDirection;
import org.project.exception.ResourceUnsupportedException;
import org.project.utils.specification.search.SearchCriteria;
import org.project.utils.specification.search.SearchSpecification;
import org.project.utils.specification.sort.SortCriteria;
import org.project.utils.specification.sort.SortSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class SpecificationUtils<T> {
    private Specification<T> specification = Specification.allOf();
    private SearchSpecification<T> searchSpecification;
    private SortSpecification<T> sortSpecification;

    @Autowired
    public void setSearchSpecification(SearchSpecification<T> searchSpecification) {
        this.searchSpecification = searchSpecification;
    }

    @Autowired
    public void setSortSpecification(SortSpecification<T> sortSpecification) {
        this.sortSpecification = sortSpecification;
    }

    public SpecificationUtils<T> addSearchCriteria(SearchCriteria searchCriteria, LogicalOperator logicalOperator) {
        Specification<T> newSpecification = getSearchSpecification(searchCriteria);
        return addSpecification(newSpecification, logicalOperator);
    }

    public SpecificationUtils<T> addSortCriteria(SortCriteria sortCriteria, LogicalOperator logicalOperator) {
        Specification<T> newSpecification = getSortSpecification(sortCriteria);
        return addSpecification(newSpecification, logicalOperator);
    }

    public SpecificationUtils<T> addSpecification(Specification<T> specification, LogicalOperator logicalOperator) {
        if (specification == null) {
            return this; // Skip null specifications
        }
        switch (logicalOperator) {
            case AND -> this.specification = this.specification.and(specification);
            case OR -> this.specification = this.specification.or(specification);
            default -> throw new ResourceUnsupportedException("Unsupported operation: " + logicalOperator);
        }
        return this;
    }

    public SpecificationUtils<T> reset() {
        this.specification = Specification.allOf();
        return this;
    }

    public Specification<T> getSearchSpecification(SearchCriteria searchCriteria) {
        if (searchCriteria == null || searchCriteria.getFieldName() == null || searchCriteria.getComparisonOperator() == null || searchCriteria.getComparedValue() == null) {
            return null;
        }
        searchSpecification.setSearchCriteria(searchCriteria);
        return searchSpecification;
    }

    public Specification<T> getSortSpecification(SortCriteria sortCriteria) {
        if (sortCriteria == null || sortCriteria.getFieldName() == null || sortCriteria.getSortDirection() == null) {
            return null;
        }
        sortSpecification.setSortCriteria(sortCriteria);
        return sortSpecification;
    }

    public Specification<T> getSearchSpecification(String fieldName, ComparisonOperator comparisonOperator, Object comparedValue, JoinType joinType) {
        if (fieldName == null || comparisonOperator == null || comparedValue == null) {
            return null;
        }

        searchSpecification.setSearchCriteria(SearchCriteria.builder()
                .fieldName(fieldName)
                .comparisonOperator(comparisonOperator)
                .comparedValue(comparedValue)
                .joinType(joinType)
                .build());
        return searchSpecification;
    }

    public Specification<T> getSortSpecification(String fieldName, AggregationFunction aggregationFunction, SortDirection sortDirection, JoinType joinType) {
        if (fieldName == null || aggregationFunction == null || sortDirection == null) {
            return null;
        }
        sortSpecification.setSortCriteria(SortCriteria.builder()
                .fieldName(fieldName)
                .aggregationFunction(aggregationFunction)
                .sortDirection(sortDirection)
                .joinType(joinType)
                .build());
        return sortSpecification;
    }

    public Specification<T> getSearchSpecifications(SearchCriteria... searchCriterias) {
        return getSearchSpecifications(Arrays.asList(searchCriterias));
    }

    public Specification<T> getSortSpecifications(SortCriteria... sortCriterias) {
        return getSortSpecifications(Arrays.asList(sortCriterias));
    }

    public Specification<T> getSearchSpecifications(List<SearchCriteria> searchCriterias) {
        Specification<T> specification = Specification.allOf();
        for (SearchCriteria searchCriteria : searchCriterias) {
            Specification<T> newSpecification = getSearchSpecification(searchCriteria);
            if (newSpecification == null) {
                continue; // Skip invalid search criteria
            }
            specification = specification.and(newSpecification);
        }
        return specification;
    }

    public Specification<T> getSortSpecifications(List<SortCriteria> sortCriterias) {
        Specification<T> specification = Specification.allOf();
        for (SortCriteria sortCriteria : sortCriterias) {
            Specification<T> newSpecification = getSortSpecification(sortCriteria);
            if (newSpecification == null) {
                continue; // Skip invalid sort criteria
            }
            specification = specification.and(newSpecification);
        }
        return specification;
    }

    public Specification<T> getSpecifications(List<SearchCriteria> searchCriterias, List<SortCriteria> sortCriterias) {
        Specification<T> searchSpec = getSearchSpecifications(searchCriterias);
        Specification<T> sortSpec = getSortSpecifications(sortCriterias);
        return searchSpec.and(sortSpec);
    }
}
