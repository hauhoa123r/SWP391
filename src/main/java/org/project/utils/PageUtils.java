package org.project.utils;

import jakarta.persistence.TypedQuery;
import org.project.exception.page.InvalidPageException;
import org.project.exception.page.PageNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PageUtils<T> {
    public Page<T> getEntitiesByPage(TypedQuery<T> getEntitiesTypedQuery, TypedQuery<Long> countTypedQuery, Pageable pageable) {
        getEntitiesTypedQuery.setFirstResult((int) pageable.getOffset());
        getEntitiesTypedQuery.setMaxResults(pageable.getPageSize());
        List<T> entities = getEntitiesTypedQuery.getResultList();

        Long total = countTypedQuery.getSingleResult();
        return new PageImpl<>(entities, pageable, total);
    }

    public Pageable getPageable(int index, int size) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }

        return PageRequest.of(index, size);
    }

    public Pageable getPageable(int index, int size, Sort sort) {
        if (index < 0 || size <= 0) {
            throw new InvalidPageException(index, size);
        }

        return PageRequest.of(index, size, sort);
    }

    public void validatePage(Page<T> page, Class<?> entityClass) {
        if (page.isEmpty()) {
            throw new PageNotFoundException(entityClass, page.getNumber(), page.getSize());
        }
    }
}
