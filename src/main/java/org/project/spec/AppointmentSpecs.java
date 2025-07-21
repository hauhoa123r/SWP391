package org.project.spec;

import org.project.entity.AppointmentEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AppointmentSpecs {
    public static Specification<AppointmentEntity> byDoctorId(Long doctorId) {
        return (root, query, cb) -> doctorId == null ? null : cb.equal(root.get("doctorEntity").get("id"), doctorId);
    }

    public static Specification<AppointmentEntity> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String like = "%" + search.trim().toLowerCase() + "%";
            // Join tới entity bệnh nhân và search theo tên
            return cb.like(cb.lower(root.join("patientEntity").get("fullName")), like);
        };
    }

    public static Specification<AppointmentEntity> status(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return null;
            return cb.equal(cb.lower(root.get("appointmentStatus")), status.toLowerCase());
        };
    }

    public static Specification<AppointmentEntity> dateFilter(String dateFilter, String specificDate) {
        return (root, query, cb) -> {
            if ("today".equals(dateFilter)) {
                return cb.equal(root.get("startTime").as(LocalDate.class), LocalDate.now());
            } else if ("tomorrow".equals(dateFilter)) {
                return cb.equal(root.get("startTime").as(LocalDate.class), LocalDate.now().plusDays(1));
            } else if ("week".equals(dateFilter)) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.with(java.time.DayOfWeek.MONDAY);
                LocalDate end = now.with(java.time.DayOfWeek.SUNDAY);
                return cb.between(root.get("startTime").as(LocalDate.class), start, end);
            } else if ("month".equals(dateFilter)) {
                LocalDate now = LocalDate.now();
                LocalDate start = now.withDayOfMonth(1);
                LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
                return cb.between(root.get("startTime").as(LocalDate.class), start, end);
            } else if ("custom".equals(dateFilter) && specificDate != null && !specificDate.isBlank()) {
                return cb.equal(root.get("startTime").as(LocalDate.class), LocalDate.parse(specificDate));
            }
            return null;
        };
    }
}
