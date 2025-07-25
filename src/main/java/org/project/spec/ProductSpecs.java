package org.project.spec;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.project.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<ProductEntity> containsName(String keyword) {
        return (Root<ProductEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (keyword == null || keyword.isEmpty()) {
                return cb.conjunction(); // tất cả
            }
            return cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
        };
    }
}
