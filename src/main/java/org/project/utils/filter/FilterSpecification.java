package org.project.utils.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.project.enums.Operation;
import org.project.exception.ResourceUnsupportedException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterSpecification<T> {
    private Specification<T> specification = Specification.allOf();

    public FilterSpecification<T> addSearchCriteria(SearchCriteria searchCriteria, Operation operation) {
        Specification<T> newSpecification = getSpecification(searchCriteria);
        if (newSpecification != null) {
            switch (operation) {
                case AND -> specification = specification.and(newSpecification);
                case OR -> specification = specification.or(newSpecification);
                default -> throw new ResourceUnsupportedException("Unsupported operation: " + operation);
            }
        }

        return this;
    }

    public Specification<T> getSpecification(SearchCriteria searchCriteria) {
        if (searchCriteria == null || searchCriteria.getFieldName() == null || searchCriteria.getOperation() == null || searchCriteria.getComparedValue() == null) {
            return Specification.allOf();
        }
        return new GenericSpecification<>(searchCriteria);
    }

    public Specification<T> getSpecification(String fieldName, Operation operation, Object comparedValue) {
        if (fieldName == null || operation == null || comparedValue == null) {
            return Specification.allOf();
        }
        return new GenericSpecification<>(new SearchCriteria(fieldName, operation, comparedValue));
    }

    public Specification<T> getSpecifications(SearchCriteria... searchCriterias) {
        Specification<T> specification = Specification.allOf();
        for (SearchCriteria searchCriteria : searchCriterias) {
            specification = specification.and(getSpecification(searchCriteria));
        }
        return specification;
    }

    public Specification<T> getSpecifications(List<SearchCriteria> searchCriterias) {
        Specification<T> specification = Specification.allOf();
        for (SearchCriteria searchCriteria : searchCriterias) {
            specification = specification.and(getSpecification(searchCriteria));
        }
        return specification;
    }
}
