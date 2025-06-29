package org.project.repository;

import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByDoctorEntityStaffEntityIdAndStartTimeBetween(Long doctorEntityStaffEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    boolean existsByPatientEntityIdAndStartTimeEquals(Long patientEntityId, Timestamp startTime);

    boolean existsByDoctorEntityIdAndStartTimeEquals(Long doctorEntityId, Timestamp startTime);

    Collection<? extends AppointmentEntity> findByPatientEntityIdAndStartTimeBetween(Long patientEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    List<AppointmentEntity> findByDoctorEntity_StaffEntity_HospitalEntity_IdAndAppointmentStatus(Long hospitalId, AppointmentStatus appointmentStatus);
}
