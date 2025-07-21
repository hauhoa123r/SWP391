package org.project.repository;

import org.project.entity.StaffScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public interface StaffScheduleRepository extends JpaRepository<StaffScheduleEntity, Long> {
    @Query("SELECT COUNT(sc) > 0 FROM StaffScheduleEntity sc WHERE sc.staffEntity.id = :staffId AND sc.availableDate = CURRENT_DATE")
    boolean existsByStaffIdAndAvailableDateToday(@Param("staffId") Long staffId);

    List<StaffScheduleEntity> findByStaffEntityIdAndAvailableDate(Long staffEntityId, Date availableDate);
}
