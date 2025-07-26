package org.project.repository;

import org.project.entity.StaffScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface StaffScheduleRepository extends JpaRepository<StaffScheduleEntity, Long> {
    List<StaffScheduleEntity> findByStaffEntityIdAndAvailableDate(Long staffEntityId, Date availableDate);
}
