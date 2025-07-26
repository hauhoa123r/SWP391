package org.project.admin.repository;

import org.project.admin.entity.Appointment;
import org.project.admin.enums.appoinements.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository("adminAppointmentRepository")
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByDoctor_StaffId(Long doctorId, Pageable pageable);
    Page<Appointment> findByPatient_PatientId(Long patientId, Pageable pageable);
    Page<Appointment> findByAppointmentStatus(AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByStartTimeAfterAndAppointmentStatusIn(
            LocalDateTime now,
            List<AppointmentStatus> statusList,
            Pageable pageable
    );

    Page<Appointment> findByStartTimeAfter(LocalDateTime now, Pageable pageable);

//    // Kiểm tra trùng lịch bác sĩ
//    @Query("SELECT a FROM Appointment a WHERE a.doctor.staffId = :doctorId " +
//           "AND a.appointmentStatus NOT IN (:excludedStatuses) " +
//           "AND (:startTime < FUNCTION('DATE_ADD', a.startTime, a.durationMinutes, 'MINUTE') " +
//           "AND a.startTime < :endTime)")
//    List<Appointment> findConflictingDoctorAppointments(
//            @Param("doctorId") Long doctorId,
//            @Param("startTime") LocalDateTime startTime,
//            @Param("endTime") LocalDateTime endTime,
//            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
//    );
//
//    // Kiểm tra trùng lịch bệnh nhân
//    @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId " +
//           "AND a.appointmentStatus NOT IN (:excludedStatuses) " +
//           "AND (:startTime < FUNCTION('DATE_ADD', a.startTime, a.durationMinutes, 'MINUTE') " +
//           "AND a.startTime < :endTime)")
//    List<Appointment> findConflictingPatientAppointments(
//            @Param("patientId") Long patientId,
//            @Param("startTime") LocalDateTime startTime,
//            @Param("endTime") LocalDateTime endTime,
//            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
//    );
//
//    // Kiểm tra trùng lịch bác sĩ (loại trừ appointment hiện tại khi update)
//    @Query("SELECT a FROM Appointment a WHERE a.doctor.staffId = :doctorId " +
//           "AND a.appointmentId != :excludeAppointmentId " +
//           "AND a.appointmentStatus NOT IN (:excludedStatuses) " +
//           "AND (:startTime < FUNCTION('DATE_ADD', a.startTime, a.durationMinutes, 'MINUTE') " +
//           "AND a.startTime < :endTime)")
//    List<Appointment> findConflictingDoctorAppointmentsExcluding(
//            @Param("doctorId") Long doctorId,
//            @Param("startTime") LocalDateTime startTime,
//            @Param("endTime") LocalDateTime endTime,
//            @Param("excludeAppointmentId") Long excludeAppointmentId,
//            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
//    );
//
//    // Kiểm tra trùng lịch bệnh nhân (loại trừ appointment hiện tại khi update)
//    @Query("SELECT a FROM Appointment a WHERE a.patient.patientId = :patientId " +
//           "AND a.appointmentId != :excludeAppointmentId " +
//           "AND a.appointmentStatus NOT IN (:excludedStatuses) " +
//           "AND (:startTime < FUNCTION('DATE_ADD', a.startTime, a.durationMinutes, 'MINUTE') " +
//           "AND a.startTime < :endTime)")
//    List<Appointment> findConflictingPatientAppointmentsExcluding(
//            @Param("patientId") Long patientId,
//            @Param("startTime") LocalDateTime startTime,
//            @Param("endTime") LocalDateTime endTime,
//            @Param("excludeAppointmentId") Long excludeAppointmentId,
//            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
//    );
}
