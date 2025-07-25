package org.project.repository;

import org.project.entity.StaffScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface StaffScheduleRepository extends JpaRepository<StaffScheduleEntity, Long> {
    @Modifying
    @Query("""
            SELECT sse
            FROM StaffScheduleEntity sse
            JOIN sse.staffEntity se
            LEFT JOIN se.leaveRequestEntities lre WITH lre.status = 'APPROVED'
                 AND lre.startDate <= ?2 AND lre.endDate >= ?2
            WHERE sse.staffEntity.id = ?1
              AND sse.availableDate = ?2
              AND lre.id IS NULL
            """)
    List<StaffScheduleEntity> findByStaffEntityIdAndAvailableDate(Long staffEntityId, Date availableDate);
}
