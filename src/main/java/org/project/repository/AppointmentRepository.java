package org.project.repository;

import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByDoctorEntityStaffEntityIdAndStartTimeBetween(Long doctorEntityStaffEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    boolean existsByPatientEntityIdAndStartTimeEquals(Long patientEntityId, Timestamp startTime);

    boolean existsByDoctorEntityIdAndStartTimeEquals(Long doctorEntityId, Timestamp startTime);

    @Query("select count(id) from AppointmentEntity \n" +
            "where date(startTime) = now()")
    int countTotalAppointmentsToday();


    Collection<? extends AppointmentEntity> findByPatientEntityIdAndStartTimeBetween(Long patientEntityId, Timestamp startTimeAfter, Timestamp startTimeBefore);

    Long countByAppointmentStatusAndServiceEntityDepartmentEntityId(AppointmentStatus appointmentStatus, Long serviceEntityDepartmentEntityId);

    Long countByAppointmentStatus(AppointmentStatus appointmentStatus);

}
