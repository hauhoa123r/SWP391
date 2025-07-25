package org.project.spec;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import org.project.entity.AppointmentEntity;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public static Specification<AppointmentEntity> dateFilter(String dateFilter, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            if (dateFilter == null && startDate == null) {
                return null;
            }

            // Lấy trường startTime dưới dạng Timestamp
            Expression<Timestamp> startTime = root.get("startTime");

            if (dateFilter != null) {
                LocalDate now = LocalDate.now();
                switch (dateFilter) {
                    case "today":
                        return cb.between(startTime,
                                Timestamp.valueOf(now.atStartOfDay()),
                                Timestamp.valueOf(now.atTime(23, 59, 59)));
                    case "tomorrow":
                        LocalDate tomorrow = now.plusDays(1);
                        return cb.between(startTime,
                                Timestamp.valueOf(tomorrow.atStartOfDay()),
                                Timestamp.valueOf(tomorrow.atTime(23, 59, 59)));
                    case "week":
                        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
                        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
                        return cb.between(startTime,
                                Timestamp.valueOf(startOfWeek.atStartOfDay()),
                                Timestamp.valueOf(endOfWeek.atTime(23, 59, 59)));
                    case "month":
                        LocalDate startOfMonth = now.withDayOfMonth(1);
                        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
                        return cb.between(startTime,
                                Timestamp.valueOf(startOfMonth.atStartOfDay()),
                                Timestamp.valueOf(endOfMonth.atTime(23, 59, 59)));
                }
            }

            if (startDate != null && endDate != null) {
                return cb.between(startTime,
                        Timestamp.valueOf(startDate.atStartOfDay()),
                        Timestamp.valueOf(endDate.atTime(23, 59, 59)));
            }

            return null;
        };
    }
    public static Specification<AppointmentEntity> sortByTimeOfDay(String sortOrder) {
        return (root, query, cb) -> {
            if (sortOrder == null || sortOrder.isEmpty()) {
                return null;
            }

            Expression<Integer> hour = cb.function("HOUR", Integer.class, root.get("startTime"));
            Expression<Integer> minute = cb.function("MINUTE", Integer.class, root.get("startTime"));

            Order timeOrder = sortOrder.equalsIgnoreCase("asc")
                    ? cb.asc(hour) : cb.desc(hour);
            Order minuteOrder = sortOrder.equalsIgnoreCase("asc")
                    ? cb.asc(minute) : cb.desc(minute);

            query.orderBy(timeOrder, minuteOrder);
            return null;
        };
    }
}
