package org.project.repository;

import org.project.entity.AppointmentEntity;
import org.project.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository <MedicalRecordEntity, Long>{
    List<MedicalRecordEntity> findByPatientEntity_Id(Long patientId);

    Optional<Object> findByAppointmentEntity(AppointmentEntity appointmentEntity);

    MedicalRecordEntity findByAppointmentEntity_Id(Long appointmentEntityId);
}