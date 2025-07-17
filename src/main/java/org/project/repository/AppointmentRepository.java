package org.project.repository;

import jakarta.transaction.Transactional;
import org.project.entity.AppointmentEntity;
import org.project.entity.SchedulingCoordinatorEntity;
import org.project.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByDoctorEntityStaffEntityIdAndStartTimeBetween(Long doctorEntityStaffEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    boolean existsByPatientEntityIdAndStartTimeEquals(Long patientEntityId, Timestamp startTime);

    boolean existsByDoctorEntityIdAndStartTimeEquals(Long doctorEntityId, Timestamp startTime);

    Collection<? extends AppointmentEntity> findByPatientEntityIdAndStartTimeBetween(Long patientEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    List<AppointmentEntity> findByDoctorEntity_StaffEntity_HospitalEntity_IdAndAppointmentStatus(Long hospitalId, AppointmentStatus appointmentStatus);

    @Transactional
    @Modifying
    @Query("UPDATE AppointmentEntity a SET a.appointmentStatus = :status, a.schedulingCoordinatorEntity.id = :coordinatorId WHERE a.id = :appointmentId")
    int updateAppointmentStatus(@Param("appointmentId") Long appointmentId,
                                @Param("status") AppointmentStatus status,
                                @Param("coordinatorId") Long schedulingCoordinatorId);

    @Query("SELECT a FROM AppointmentEntity a WHERE" +
           "((a.doctorEntity.id = :doctorId OR a.patientEntity.id =:patientId))" +
           "AND a.startTime < :endTime " +
           "AND a.startTime >= :startTime")
    List<AppointmentEntity> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId,
            @Param("startTime") Timestamp startTime,
            @Param("endTime") Timestamp endTime
    );
}
