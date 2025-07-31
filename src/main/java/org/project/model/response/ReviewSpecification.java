package org.project.model.response;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.project.entity.PatientEntity;
import org.project.entity.ReviewEntity;
import org.project.enums.ReviewStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ReviewSpecification {
    public static Specification<ReviewEntity> filter(
            String search,
            Integer rating,
            ReviewStatus status
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo tên người đánh giá hoặc nội dung
            if (search != null && !search.trim().isEmpty()) {
                Join<ReviewEntity, PatientEntity> patientJoin = root.join("patientEntity", JoinType.LEFT);
                Predicate byFullName = cb.like(cb.lower(patientJoin.get("fullName")), "%" + search.toLowerCase() + "%");
                Predicate byContent = cb.like(cb.lower(root.get("content")), "%" + search.toLowerCase() + "%");
                predicates.add(cb.or(byFullName, byContent));
            }

            // Lọc theo số sao đánh giá
            if (rating != null) {
                predicates.add(cb.equal(root.get("rating"), rating));
            }

            // Lọc theo trạng thái (APPROVED / PENDING / HIDDEN)
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
