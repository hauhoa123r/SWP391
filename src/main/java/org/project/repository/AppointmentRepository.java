package org.project.repository;

import org.project.entity.AppointmentEntity;
import org.project.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity,Long>{
    List<AppointmentEntity> findByDoctorEntityIdAndAppointmentStatusIn(Long dortorId, List<AppointmentStatus> statuses);

}
