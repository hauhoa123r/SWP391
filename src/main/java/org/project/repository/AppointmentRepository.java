package org.project.repository;

import jakarta.transaction.Transactional;
import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>, JpaSpecificationExecutor<AppointmentEntity> {
    List<AppointmentEntity> findByDoctorEntityStaffEntityIdAndStartTimeBetween(Long doctorEntityStaffEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    boolean existsByPatientEntityIdAndStartTimeEquals(Long patientEntityId, Timestamp startTime);

    boolean existsByDoctorEntityIdAndStartTimeEquals(Long doctorEntityId, Timestamp startTime);

    @Query("select count(id) from AppointmentEntity \n" +
            "where date(startTime) = now()")
    int countTotalAppointmentsToday();

    Collection<? extends AppointmentEntity> findByPatientEntityIdAndStartTimeBetween(Long patientEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    Long countByAppointmentStatusAndServiceEntityDepartmentEntityId(AppointmentStatus appointmentStatus, Long serviceEntityDepartmentEntityId);

    Long countByAppointmentStatus(AppointmentStatus appointmentStatus);

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

    @Modifying
    @Query("UPDATE AppointmentEntity a SET a.doctorEntity.id = :substituteId " +
            "WHERE a.doctorEntity.id = :staffId " +
            "AND DATE(a.startTime) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    void transferFullDayAppointments(
            @Param("staffId") Long staffId,
            @Param("substituteId") Long substituteId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    @Modifying
    @Query("UPDATE AppointmentEntity a SET a.doctorEntity.id = :substituteId " +
            "WHERE a.doctorEntity.id = :staffId " +
            "AND DATE(a.startTime) = DATE(:date) " +
            "AND TIME(a.startTime) >= '08:00:00' " +
            "AND TIME(a.startTime) < '12:00:00'")
    void transferMorningShiftAppointments(
            @Param("staffId") Long staffId,
            @Param("substituteId") Long substituteId,
            @Param("date") Timestamp date
    );

    @Modifying
    @Query("UPDATE AppointmentEntity a SET a.doctorEntity.id = :substituteId " +
            "WHERE a.doctorEntity.id = :staffId " +
            "AND DATE(a.startTime) = DATE(:date) " +
            "AND TIME(a.startTime) >= '13:00:00' " +
            "AND TIME(a.startTime) < '17:00:00'")
    void transferAfternoonShiftAppointments(
            @Param("staffId") Long staffId,
            @Param("substituteId") Long substituteId,
            @Param("date") Timestamp date
    );

    List<AppointmentEntity> findTop5ByPatientEntity_UserEntity_IdOrderByIdDesc(Long userId);
}
