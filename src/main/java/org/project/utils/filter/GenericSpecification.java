package org.project.utils.filter;

import jakarta.persistence.criteria.*;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"unchecked", "rawtypes"})
public class GenericSpecification<T> implements Specification<T> {
    private SearchCriteria serchCriteria;

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        // Tạo đường dẫn truy cập thuộc tính
        Path<Object> realPath;

        // Kiểm tra xem có phải thuộc tính lồng nhau không
        if (serchCriteria.getFieldName().contains(".")) {
            String[] fieldParts = serchCriteria.getFieldName().split("\\.");
            realPath = root.get(fieldParts[0]);
            for (int i = 1; i < fieldParts.length; i++) {
                realPath = realPath.get(fieldParts[i]);
            }
        } else {
            realPath = root.get(serchCriteria.getFieldName());
        }

        // Thực hiện các phép so sánh với realPath thay vì root.get()
        switch (serchCriteria.getOperation()) {
            case EQUALS -> {
                return builder.equal(realPath, serchCriteria.getComparedValue());
            }
            case NOT_EQUALS -> {
                return builder.notEqual(realPath, serchCriteria.getComparedValue());
            }
            case GREATER_THAN -> {
                return builder.greaterThan(realPath.as(Comparable.class), (Comparable) serchCriteria.getComparedValue());
            }
            case LESS_THAN -> {
                return builder.lessThan(realPath.as(Comparable.class), (Comparable) serchCriteria.getComparedValue());
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                return builder.greaterThanOrEqualTo(realPath.as(Comparable.class), (Comparable) serchCriteria.getComparedValue());
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                return builder.lessThanOrEqualTo(realPath.as(Comparable.class), (Comparable) serchCriteria.getComparedValue());
            }
            case LIKE -> {
                return builder.like(realPath.as(String.class), serchCriteria.getComparedValue().toString());
            }
            case NOT_LIKE -> {
                return builder.notLike(realPath.as(String.class), serchCriteria.getComparedValue().toString());
            }
            case CONTAINS -> {
                return builder.like(realPath.as(String.class), "%" + serchCriteria.getComparedValue() + "%");
            }
            case NOT_CONTAINS -> {
                return builder.notLike(realPath.as(String.class), "%" + serchCriteria.getComparedValue() + "%");
            }
            case IN -> {
                return realPath.in(serchCriteria.getComparedValue());
            }
            case NOT_IN -> {
                return builder.not(realPath.in(serchCriteria.getComparedValue()));
            }
            case IS_NULL -> {
                return builder.isNull(realPath);
            }
            case IS_NOT_NULL -> {
                return builder.isNotNull(realPath);
            }
            case BETWEEN -> {
                Object[] values = (Object[]) serchCriteria.getComparedValue();
                return builder.between(
                        realPath.as(Comparable.class),
                        (Comparable) values[0],
                        (Comparable) values[1]
                );
            }
            case NOT_BETWEEN -> {
                Object[] values = (Object[]) serchCriteria.getComparedValue();
                return builder.not(
                        builder.between(
                                realPath.as(Comparable.class),
                                (Comparable) values[0],
                                (Comparable) values[1]
                        )
                );
            }
        }

        return null;
    }
}
