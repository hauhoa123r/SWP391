package org.project.specification;

import jakarta.persistence.criteria.Predicate;
import org.project.entity.PatientEntity;
import org.project.model.request.AdminPatientRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AdminPatientSpecification {
    public static Specification<PatientEntity> filter(AdminPatientRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), keyword),
                        cb.like(cb.lower(root.get("email")), keyword),
                        cb.like(cb.lower(root.get("phoneNumber")), keyword)
                ));
            }

            if (request.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), request.getGender()));
            }

            if (request.getBloodType() != null) {
                predicates.add(cb.equal(root.get("bloodType"), request.getBloodType()));
            }

            if (request.getBirthdateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthdate"), Date.valueOf(request.getBirthdateFrom())));
            }

            if (request.getBirthdateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("birthdate"), Date.valueOf(request.getBirthdateTo())));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
