package org.project.specification;

import org.project.entity.PatientEntity;
import org.project.enums.Gender;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Reusable Specifications for advanced AdminPatient search.
 */
public final class AdminPatientSpecifications {
    private AdminPatientSpecifications() {}

    private static String like(String kw) {
        return "%" + kw.toLowerCase() + "%";
    }

    public static Specification<PatientEntity> fullNameContains(String kw) {
        return (root, q, cb) -> kw == null || kw.isBlank() ? cb.conjunction() :
                cb.like(cb.lower(root.get("fullName")), like(kw));
    }

    public static Specification<PatientEntity> emailContains(String kw) {
        return (root, q, cb) -> kw == null || kw.isBlank() ? cb.conjunction() :
                cb.like(cb.lower(root.get("email")), like(kw));
    }

    public static Specification<PatientEntity> phoneContains(String kw) {
        return (root, q, cb) -> kw == null || kw.isBlank() ? cb.conjunction() :
                cb.like(cb.lower(root.get("phoneNumber")), like(kw));
    }

    public static Specification<PatientEntity> genderEquals(Gender gender) {
        return (root, q, cb) -> gender == null ? cb.conjunction() : cb.equal(root.get("gender"), gender);
    }

    public static Specification<PatientEntity> idIn(Collection<Long> ids) {
        return (root, q, cb) -> (ids == null || ids.isEmpty()) ? cb.conjunction() : root.get("id").in(ids);
    }

    public static Specification<PatientEntity> birthdateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return (r, q, cb) -> cb.conjunction();
        if (from == null) from = to;
        if (to == null) to = from;
        LocalDate f = from, t = to;
        return (root, q, cb) -> cb.between(root.get("dateOfBirth"), f, t);
    }

    /**
     * Global OR search against name/email/phone.
     */
    public static Specification<PatientEntity> globalKeyword(String kw) {
        if (kw == null || kw.isBlank()) return (r, q, cb) -> cb.conjunction();
        String like = like(kw);
        return (root, q, cb) -> cb.or(
                cb.like(cb.lower(root.get("fullName")), like),
                cb.like(cb.lower(root.get("email")), like),
                cb.like(cb.lower(root.get("phoneNumber")), like)
        );
    }
}