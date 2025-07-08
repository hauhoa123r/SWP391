package org.project.utils.specification.sort;

import jakarta.persistence.criteria.*;
import lombok.*;
import org.project.enums.operation.SortDirection;
import org.project.exception.ResourceUnsupportedException;
import org.project.utils.NumberTypeUtils;
import org.project.utils.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class SortSpecification<T> implements Specification<T> {
    private SortCriteria sortCriteria;
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
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        // Tạo đường dẫn truy cập thuộc tính
        Path<Object> realPath = pathUtils.getRealPath(root, sortCriteria.getFieldName(), sortCriteria.getJoinType());

        Class<?> javaType = root.getJavaType();

        switch (sortCriteria.getAggregationFunction()) {
            case NONE -> {
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(realPath)
                                : criteriaBuilder.desc(realPath)
                );
                return criteriaBuilder.conjunction();
            }
            case COUNT -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.count(realPath))
                                : criteriaBuilder.desc(criteriaBuilder.count(realPath))
                );
                return criteriaBuilder.conjunction();
            }
            case SUM -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.sum(pathUtils.getPath(realPath, javaType)))
                );
                return criteriaBuilder.conjunction();
            }
            case AVG -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.avg(pathUtils.getPath(realPath, javaType)))
                );
                return criteriaBuilder.conjunction();
            }
            case MAX -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.max(pathUtils.getPath(realPath, javaType)))
                );
                return criteriaBuilder.conjunction();
            }
            case MIN -> {
                query.groupBy(root);
                query.orderBy(
                        sortCriteria.getSortDirection().equals(SortDirection.ASC)
                                ? criteriaBuilder.asc(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)))
                                : criteriaBuilder.desc(criteriaBuilder.min(pathUtils.getPath(realPath, javaType)))
                );
                return criteriaBuilder.conjunction();
            }
            default -> throw new ResourceUnsupportedException("Unsupported sort criteria: " + sortCriteria);
        }
    }
}
