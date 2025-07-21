package org.project.repository;

import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AppointmentVRepository extends JpaRepository<AppointmentEntity,Long>, JpaSpecificationExecutor<AppointmentEntity> {
    List<AppointmentEntity> findByDoctorEntityIdAndAppointmentStatusIn(Long dortorId, List<AppointmentStatus> statuses);

    @Query("""
        FROM AppointmentEntity a
        WHERE a.doctorEntity.id = :doctorId
          AND a.startTime >= :start
          AND a.startTime < :end
        ORDER BY a.startTime
        """)
    List<AppointmentEntity> findTodayAppointmentsByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("start") Timestamp start,
            @Param("end") Timestamp end
    );


}
